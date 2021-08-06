package de.hsaalen.cmt.network.dto.rsocket

/**
 * Data transfer object when any reference added/removed.
 */
sealed class ReferenceUpdateDto : LiveDto()

/**
 * After a reference got updated by user.
 */
typealias ReferenceUpdateEvent = ReferenceUpdateDto
