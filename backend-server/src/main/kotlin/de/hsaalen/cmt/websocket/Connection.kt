package de.hsaalen.cmt.websocket

import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.repository.DocumentRepository
import de.hsaalen.cmt.session.jwt.readJwtCookie
import de.hsaalen.cmt.session.withWebSocketSession
import de.hsaalen.cmt.utils.JsonHelper
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import mu.KLogger
import mu.KotlinLogging
import org.koin.ktor.ext.inject
import java.util.*

/**
 * Delegate proxy for the websocket sessions. Extends functionality by new socket-id feature.
 */
class Connection(socket: WebSocketSession, private val context: ApplicationCall) : WebSocketSession by socket {

    /**
     * The IP address of the connected websocket.
     */
    private val ipAddress: String
        get() = context.request.origin.remoteHost

    /**
     * The email-address of the user related to this websocket instance.
     */
    private val userEmail: String
        get() = context.request.readJwtCookie().email

    /**
     * A unique id for this socket instance.
     */
    val socketId: String

    /**
     * Local logger instance related to this [Connection] instance.
     */
    val logger: KLogger

    init {
        // Calculate unique id of this connection
        val uniqueData = (hashCode().toString() + System.nanoTime() + ipAddress).encodeToByteArray()
        socketId = UUID.nameUUIDFromBytes(uniqueData).toString()
        logger = KotlinLogging.logger("$userEmail@$socketId")
    }

    /**
     * Process all incoming packets using suspend function to prevent blocking.
     */
    suspend fun suspendProcessing() {
        logger.info("websocket: connected")

        withWebSocketSession(userEmail, socketId) {
            val repo: DocumentRepository by context.inject()

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val jsonText = frame.readText()
                    logger.debug("websocket: received: $jsonText")
                    val dto: DocumentChangeDto = JsonHelper.decode(jsonText)
                    repo.modifyDocument(dto)
                } else {
                    logger.warn("websocket: received unknown frame: " + frame.frameType.name)
                }
            }
        }
    }

}
