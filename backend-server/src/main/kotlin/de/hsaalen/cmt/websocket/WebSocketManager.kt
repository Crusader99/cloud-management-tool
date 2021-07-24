package de.hsaalen.cmt.websocket

import de.hsaalen.cmt.network.dto.websocket.LiveDto
import de.hsaalen.cmt.utils.SerializeHelper
import de.hsaalen.cmt.utils.protobufData
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
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
        val payload = dto.toPayload()
        for (others in connections.filter { it.socketId != excludeSocketId }) {
            others.fireAndForget(payload)
        }
    }

    /**
     * Broadcast a DTO to all web-socket clients.
     */
    suspend fun broadcast(dto: LiveDto) {
        val payload = dto.toPayload()
        for (others in connections) {
            others.fireAndForget(payload)
        }
    }

    /**
     * Disconnect all clients with related JWT token.
     */
    suspend fun disconnect(withJwtToken: String) {
        for (others in connections.filter { it.jwtToken == withJwtToken }) {
            others.disconnect()
        }
    }

    /**
     * Convert LiveDto to payload that can be used for rSocket transmission.
     */
    private fun LiveDto.toPayload(): Payload {
        logger.debug("broadcast: " + this::class.simpleName)
        logger.debug(SerializeHelper.encodeJson(this))
        return buildPayload {
            protobufData(this@toPayload)
        }
    }
}
