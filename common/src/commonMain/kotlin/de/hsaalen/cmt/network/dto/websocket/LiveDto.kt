package de.hsaalen.cmt.network.dto.websocket

import de.hsaalen.cmt.crypto.Encryptable
import de.hsaalen.cmt.events.Event
import kotlinx.serialization.Serializable

/**
 * DTO that is sent over websocket. ("live synchronization")
 */
@Serializable
sealed class LiveDto : Event, Encryptable<LiveDto>
