package de.hsaalen.cmt.events

import de.hsaalen.cmt.events.server.LabelChangeEvent
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateEvent
import de.hsaalen.cmt.rsocket.WebSocketManager

/**
 * Contains handlers for events that should be synchronized over different websocket instances.
 */
object EventHandlers {

    /**
     * Initialize required event handlers for synchronization.
     */
    fun init() {
        GlobalEventDispatcher.createBundle(this) {
            register(::handleReferenceUpdate)
            register(::handleLabelChange)
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

}
