package de.hsaalen.cmt.websocket

import de.hsaalen.cmt.network.dto.websocket.LiveDto
import de.hsaalen.cmt.utils.JsonHelper
import io.ktor.http.cio.websocket.*
import mu.KotlinLogging

/**
 * Singleton object for holding active web socket connections.
 */
object WebSocketManager {

    /**
     * List of all currently active web-socket connections.
     */
    val connections = mutableListOf<Connection>()

    /**
     * Local logger instance for this [WebSocketManager].
     */
    private val logger = KotlinLogging.logger { }

    /**
     * Broadcast a DTO to all web-socket clients except to the (own) client.
     */
    suspend fun broadcastExcept(excludeSocketId: String, dto: LiveDto) {
        val jsonText = JsonHelper.encode(dto)
        logger.trace("send: $jsonText")
        for (others in connections.filter { it.socketId != excludeSocketId }) {
            others.outgoing.send(Frame.Text(jsonText))
        }
    }

    /**
     * Broadcast a DTO to all web-socket clients.
     */
    suspend fun broadcast(dto: LiveDto) {
        val jsonText = JsonHelper.encode(dto)
        logger.trace("send: $jsonText")
        for (others in connections) {
            others.outgoing.send(Frame.Text(jsonText))
        }
    }

}
