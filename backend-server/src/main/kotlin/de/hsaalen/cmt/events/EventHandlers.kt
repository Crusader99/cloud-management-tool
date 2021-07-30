package de.hsaalen.cmt.events

import de.hsaalen.cmt.events.server.LabelChangeEvent
import de.hsaalen.cmt.events.server.SessionCloseEvent
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateEvent
import de.hsaalen.cmt.rsocket.WebSocketManager
import mu.KotlinLogging

/**
 * Contains handlers for events that should be synchronized over different websocket instances.
 */
object EventHandlers {

    /**
     * Local logger instance for this object.
     */
    private val logger = KotlinLogging.logger { }

    /**
     * Initialize required event handlers for synchronization.
     */
    fun init() {
        GlobalEventDispatcher.createBundle(this) {
            register(::handleReferenceUpdate)
            register(::handleLabelChange)
            register(::handleSessionClose)
        }
    }

    /**
     * Invoked when reference was added/removed.
     */
    private suspend fun handleReferenceUpdate(event: ReferenceUpdateEvent) {
        WebSocketManager.broadcast(event)
    }

    /**
     * Invoked when user adds/removed labels.
     */
    private suspend fun handleLabelChange(event: LabelChangeEvent) {
        WebSocketManager.broadcast(event.modification)
    }

    /**
     * Called when user performs logout.
     */
    private suspend fun handleSessionClose(event: SessionCloseEvent) {
        try {
            WebSocketManager.disconnect(event.jwtToken)
        } catch (ex: Exception) {
            logger.warn("Unable to disconnect websockets related to session", ex)
        }
    }

}
