package de.hsaalen.cmt.events.handlers

import de.hsaalen.cmt.events.EventType
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.events.notify
import de.hsaalen.cmt.network.dto.rsocket.LocalSessionClosedDto
import mu.KotlinLogging

/**
 * Global event handlers to handle logouts from other browser tabs of the same session.
 */
object DisconnectEventHandlers {

    /**
     * Logging instance for this class.
     */
    private val logger = KotlinLogging.logger("DisconnectEventHandlers")

    /**
     * Initialize global disconnect event handlers.
     */
    fun init() {
        GlobalEventDispatcher.createBundle(this) {
            // Serverside events
            register(::onLocalSessionClosed)
        }
    }

    /**
     * Event called by server after another browser tab of the same session logged out.
     */
    private suspend fun onLocalSessionClosed(event: LocalSessionClosedDto) {
        logger.info { "Received LocalSessionClosedDto" }
        GlobalEventDispatcher.notify(EventType.PRE_LOGOUT, event)
    }

}
