package de.hsaalen.cmt.network.dto.websocket

import kotlinx.serialization.Serializable

/**
 * Data transfer object when any reference added/removed.
 */
@Serializable
sealed class ReferenceUpdateDto : LiveDto()

typealias ReferenceUpdateEvent = ReferenceUpdateDto
