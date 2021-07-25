package de.hsaalen.cmt.rsocket

import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.network.dto.websocket.LiveDto
import de.hsaalen.cmt.repository.DocumentRepository
import de.hsaalen.cmt.session.jwt.JwtPayload
import de.hsaalen.cmt.session.withWebSocketSession
import de.hsaalen.cmt.utils.SerializeHelper
import de.hsaalen.cmt.utils.decodeProtobufData
import io.ktor.routing.*
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketRequestHandler
import kotlinx.coroutines.cancelAndJoin
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
    val socketId: String

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
                val repo: DocumentRepository by route.inject()
                val dto: LiveDto = payload.decodeProtobufData()
                logger.debug("websocket: received: " + SerializeHelper.encodeJson(dto))
                if (dto is DocumentChangeDto) {
                    repo.modifyDocument(dto)
                } else {
                    logger.warn("Unknown data received!")
                }
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
