package de.hsaalen.cmt.rsocket

import de.hsaalen.cmt.network.dto.rsocket.LiveDto
import de.hsaalen.cmt.network.dto.rsocket.LocalSessionClosedDto
import de.hsaalen.cmt.utils.SerializeHelper
import de.hsaalen.cmt.utils.buildPayload
import mu.KotlinLogging

/**
 * A singleton object for holding active web socket connections of the current backend server. More clients may be connected
 * to other backend server instances.
 */
object LocalConnectionManager {

    /**
     * List of all currently active web-socket connections to the current server instance. More clients may be connected
     * to other backend server instances.
     */
    val connections = mutableListOf<Connection>()

    /**
     * Local logger instance for this [LocalConnectionManager].
     */
    private val logger = KotlinLogging.logger { }

    /**
     * Broadcast a DTO to all web-socket clients of the current backend server. More clients may be connected
     * to other backend server instances.
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
     * Disconnect all clients with related JWT token of the current backend server. More clients may be connected
     * to other backend server instances.
     */
    suspend fun disconnect(withJwtToken: String) {
        val payload = LocalSessionClosedDto.buildPayload()
        for (others in connections.filter { it.jwtToken == withJwtToken }) {
            try {
                // It informs the client that the session is closed by a logout,
                // performed in another browser tab of the same session
                others.fireAndForget(payload.copy())
            } finally {
                others.disconnect()
            }
        }
    }

}
