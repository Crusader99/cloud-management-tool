package de.hsaalen.cmt.events

import de.hsaalen.cmt.network.dto.websocket.ReferenceUpdateEvent
import de.hsaalen.cmt.websocket.WebSocketManager

/**
 * Contains handlers for events that should be synchronized over different websocket instances.
 */
object EventHandlers {

    /**
     * Initialize required event handlers for synchronization.
     */
    fun init() {
        GlobalEventDispatcher.register(::handleReferenceUpdate)
        GlobalEventDispatcher.register(::handleDocumentChange)
        GlobalEventDispatcher.register(::handleLabelChange)
    }

    /**
     * Invoked when reference was added/removed.
     */
    private suspend fun handleReferenceUpdate(event: ReferenceUpdateEvent) {
        WebSocketManager.broadcast(event)
    }

    /**
     * Invoked when user modified lines of a text document.
     */
    private suspend fun handleDocumentChange(event: UserDocumentChangeEvent) {
        WebSocketManager.broadcastExcept(event.senderSocketId, event.modification)
    }

    /**
     * Invoked when user adds/removed labels.
     */
    private suspend fun handleLabelChange(event: LabelChangeEvent) {
        WebSocketManager.broadcast(event.modification)
    }

}
