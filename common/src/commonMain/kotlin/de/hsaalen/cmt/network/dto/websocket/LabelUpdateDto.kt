package de.hsaalen.cmt.network.dto.websocket

import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.serialization.Serializable

/**
 * Data transfer object to describe label add or removal.
 */
@Serializable
data class LabelUpdateDto(
    val reference: UUID,
    val labelNameEncrypted: String,
    val mode: LabelChangeMode
) : LiveDto()
