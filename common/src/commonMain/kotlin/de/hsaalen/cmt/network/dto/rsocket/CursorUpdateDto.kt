package de.hsaalen.cmt.network.dto.rsocket

import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.serialization.Serializable

/**
 * Data transfer object for updating the cursor position in a text document.
 */
@Serializable
data class CursorUpdateDto(
    /**
     * Unique ID to identify the owner of the cursor.
     */
    val cursorOwner: UUID = UUID("?"),

    /**
     * The new index of the cursor. May be null, when owner of the cursor leaves the session.
     */
    val cursorIndex: Int?,
) : LiveDto() {

    /**
     * Cursor positions are not sensible information, so they are not encrypted so return same instance.
     */
    override fun encrypt() = this

    /**
     * Cursor positions are not sensible information, so they are not decrypted.
     */
    override fun decrypt() = this

}
