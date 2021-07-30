package de.hsaalen.cmt.rsocket

import de.hsaalen.cmt.network.dto.rsocket.LiveDto
import de.hsaalen.cmt.utils.SerializeHelper
import de.hsaalen.cmt.utils.buildPayload
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
     * Broadcast a DTO to all web-socket clients.
     */
    suspend fun broadcast(dto: LiveDto, filter: Iterable<Connection>.() -> Iterable<Connection> = { this }) {
        logger.debug("broadcast: " + dto::class.simpleName)
        logger.debug(SerializeHelper.encodeJson(dto))
        val payload = dto.buildPayload()
        for (others in connections.filter()) {
            // Note: A new payload has to be built for each client
            others.fireAndForget(payload.copy())
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

}
