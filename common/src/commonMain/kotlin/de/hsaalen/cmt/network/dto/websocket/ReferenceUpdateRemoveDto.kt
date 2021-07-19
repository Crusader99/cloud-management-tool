package de.hsaalen.cmt.network.dto.websocket

import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.serialization.Serializable

/**
 * Data transfer object when any reference has been removed.
 */
@Serializable
data class ReferenceUpdateRemoveDto(
    val uuid: UUID,
) : ReferenceUpdateDto()
