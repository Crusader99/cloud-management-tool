package de.hsaalen.cmt.events

import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto

/**
 * Event to be called when a user modified a line of a document.
 */
data class UserDocumentChangeEvent(
    val modification: DocumentChangeDto,
    val senderEmail: String,
    val senderSocketId: String,
) : Event
