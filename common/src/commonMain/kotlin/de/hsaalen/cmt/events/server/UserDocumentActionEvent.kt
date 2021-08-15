package de.hsaalen.cmt.events.server

import de.hsaalen.cmt.events.Event
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.rsocket.LiveDto
import kotlinx.serialization.Serializable

/**
 * Event to be called when a user changes the cursor position.
 */
@Serializable
data class UserDocumentActionEvent(
    val modification: LiveDto,
    val reference: UUID,
    val senderEmail: String,
    val senderSocketId: String,
) : Event
