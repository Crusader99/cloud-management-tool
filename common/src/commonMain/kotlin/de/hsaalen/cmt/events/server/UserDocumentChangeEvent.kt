package de.hsaalen.cmt.events.server

import de.hsaalen.cmt.events.Event
import de.hsaalen.cmt.network.dto.rsocket.DocumentChangeDto

/**
 * Event to be called when a user modified a line of a document.
 */
data class UserDocumentChangeEvent(
    val modification: DocumentChangeDto,
    val senderEmail: String,
    val senderSocketId: String,
) : Event
