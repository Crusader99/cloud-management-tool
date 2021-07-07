package de.hsaalen.cmt.network.dto.websocket

import kotlinx.serialization.Serializable

/**
 * Data transfer object when any reference has been removed.
 */
@Serializable
data class ReferenceUpdateRemoveDto(
    val uuid: String,
) : ReferenceUpdateDto()
