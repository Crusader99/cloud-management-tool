package de.hsaalen.cmt.network.dto.websocket

import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.objects.UUID

/**
 * Data transfer object to describe label add or removal.
 */
class LabelUpdateDto(
    val reference: UUID,
    val labelNameEncrypted: String,
    val mode: LabelChangeMode
) : LiveDto()
