package de.hsaalen.cmt.rsocket

import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.events.server.UserDocumentChangeEvent
import de.hsaalen.cmt.extensions.launch
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.ContentType
import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.objects.LineChangeMode
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.rsocket.*
import de.hsaalen.cmt.repository.DocumentRepository
import de.hsaalen.cmt.repository.LabelRepository
import de.hsaalen.cmt.repository.ReferenceRepository
import de.hsaalen.cmt.session.jwt.JwtPayload
import de.hsaalen.cmt.session.withWebSocketSession
import de.hsaalen.cmt.utils.SerializeHelper
import de.hsaalen.cmt.utils.buildPayload
import de.hsaalen.cmt.utils.decodeProtobufData
import io.ktor.routing.*
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketRequestHandler
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeout
import mu.KLogger
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import java.util.*

/**
 * Delegate proxy for the websocket sessions. Extends functionality by new socket-id feature.
 */
class Connection(socket: RSocket, private val payload: JwtPayload, val jwtToken: String) : RSocket by socket {

    /**
     * Local logger instance related to this [Connection] instance.
     */
    private val logger: KLogger

    /**
     * The email-address of the user related to this websocket instance.
     */
    private val userEmail: String
        get() = payload.email

    /**
     * A unique id for this socket instance.
     */
    private val socketId: String

    init {
        // Calculate unique id of this connection
        val uniqueData = (hashCode().toString() + System.nanoTime()).encodeToByteArray()
        socketId = UUID.nameUUIDFromBytes(uniqueData).toString()
        logger = KotlinLogging.logger("$userEmail@$socketId")
    }

    /**
     * Process all incoming packets using suspend function to prevent blocking.
     */
    suspend fun handler(route: Route) = RSocketRequestHandler {
        WebSocketManager.connections += this@Connection
        logger.info("Connected websocket with " + payload.email + " " + payload.fullName)
        fireAndForget { payload ->
            logger.info("got fireAndForget")
            withWebSocketSession(userEmail, socketId) {
                val dto: LiveDto = payload.decodeProtobufData()
                logger.debug("websocket: received: " + SerializeHelper.encodeJson(dto))
                if (dto is DocumentChangeDto) {
                    val repo: DocumentRepository by route.inject()
                    repo.modifyDocument(dto)
                } else if (dto is LabelUpdateDto) {
                    val repo: LabelRepository by route.inject()
                    when (dto.mode) {
                        LabelChangeMode.ADD -> repo.addLabel(dto.reference, dto.labelName)
                        LabelChangeMode.REMOVE -> repo.removeLabel(dto.reference, dto.labelName)
                    }
                } else {
                    logger.warn("Unknown data received: " + dto::class.simpleName)
                }
            }
        }

        requestChannel { init, input ->
            logger.info("got requestChannel")
            withWebSocketSession(userEmail, socketId) {
                val request: RequestDocumentDto = init.decodeProtobufData()
                val documentUUID = request.reference
                val docRepo: DocumentRepository by route.inject()
                val events = GlobalEventDispatcher.createBundle(this)

                events.launch {
                    withWebSocketSession(userEmail, socketId) {
                        input.collect {
                            try {
                                val dto: DocumentChangeDto = it.decodeProtobufData()
                                docRepo.modifyDocument(dto)
                            } catch (ex: Exception) {
                                logger.error("Unable to handle document change", ex)
                            }
                        }
                    }
                }

                val documentFlow = docRepo.downloadContent(documentUUID)
                    .lineSequence()
                    .mapIndexed { index, line ->
                        val mode = if (index == 0) LineChangeMode.MODIFY else LineChangeMode.ADD
                        DocumentChangeDto(documentUUID, index, line, mode)
                    }.asFlow()
                val eventFlow = events.receiveEventsAsFlow<UserDocumentChangeEvent>()
                    .filter { it.senderSocketId != socketId }
                    .map { it.modification }
                    .filter { it.uuid == documentUUID }

                channelFlow {
                    documentFlow.collect { send(it) }
                    eventFlow.collect { send(it) }
                }.onCompletion {
                    logger.info("Cancel document editing")
                    events.unregisterAll()
                }.map { it.buildPayload() }
            }
        }
        requestStream { payload ->
            logger.info("got requestStream")
            withWebSocketSession(userEmail, socketId) {
                val repo: ReferenceRepository by route.inject()
                val request = ClientReferenceQueryDto()
                repo.listReferences(request).map { ReferenceUpdateAddDto(it).buildPayload() }
            }
        }
        job.invokeOnCompletion {
            WebSocketManager.connections -= this@Connection
            logger.info("Websocket disconnected")
        }
    }

    /**
     * Close connection a RSocket client.
     */
    suspend fun disconnect() {
        try {
            logger.debug("Disconnecting client...")
            withTimeout(2_000) {
                job.cancelAndJoin()
            }
        } catch (t: Throwable) {
            logger.error("Unable to perform RSocket disconnect", t)
        } finally {
            WebSocketManager.connections -= this
        }
    }

}
