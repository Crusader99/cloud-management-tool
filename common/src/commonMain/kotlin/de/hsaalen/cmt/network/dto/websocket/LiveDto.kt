package de.hsaalen.cmt.network.dto.websocket

import kotlinx.serialization.Serializable

/**
 * DTO that is sent over websocket. ("live synchronization")
 */
@Serializable
sealed class LiveDto
