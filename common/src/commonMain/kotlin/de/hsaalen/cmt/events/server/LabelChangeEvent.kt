package de.hsaalen.cmt.events.server

import de.hsaalen.cmt.events.Event
import de.hsaalen.cmt.network.dto.rsocket.LabelUpdateDto
import kotlinx.serialization.Serializable

/**
 * Event to be called when a user modified a line of a document.
 */
@Serializable
data class LabelChangeEvent(
    val modification: LabelUpdateDto,
    val senderEmail: String,
) : Event
