package de.hsaalen.cmt.network.dto.websocket

import kotlinx.serialization.Serializable

/**
 * Data transfer object for editing a text document.
 */
@Serializable
data class LiveTextEditDto(
    val ref: String,
    val newTextEncrypted: String,
) : LiveDto