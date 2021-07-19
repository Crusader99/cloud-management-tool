package de.hsaalen.cmt.events

import de.hsaalen.cmt.network.dto.websocket.LabelUpdateDto

/**
 * Event to be called when a user modified a line of a document.
 */
data class LabelChangeEvent(
    val modification: LabelUpdateDto,
    val senderEmail: String,
) : Event
