package de.hsaalen.cmt.network.dto.websocket

import de.hsaalen.cmt.network.dto.objects.Reference
import kotlinx.serialization.Serializable

/**
 * Data transfer object when any reference has been added.
 */
@Serializable
data class ReferenceUpdateAddDto(
    val reference: Reference,
) : ReferenceUpdateDto()
