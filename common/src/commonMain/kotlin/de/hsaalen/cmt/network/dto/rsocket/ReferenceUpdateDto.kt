package de.hsaalen.cmt.network.dto.rsocket

import de.hsaalen.cmt.events.Event
import kotlinx.serialization.Serializable

/**
 * Data transfer object when any reference added/removed.
 */
@Serializable
sealed class ReferenceUpdateDto : Event, LiveDto()

/**
 * After a reference got updated by an user.
 */
typealias ReferenceUpdateEvent = ReferenceUpdateDto
