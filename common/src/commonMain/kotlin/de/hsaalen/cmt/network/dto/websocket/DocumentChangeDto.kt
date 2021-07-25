package de.hsaalen.cmt.network.dto.websocket

import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.crypto.encrypt
import de.hsaalen.cmt.network.dto.objects.LineChangeMode
import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.serialization.Serializable

/**
 * Data transfer object for editing a text document.
 */
@Serializable
data class DocumentChangeDto(
    val uuid: UUID,
    val lineNumber: Int,
    val lineContent: String,
    val lineChangeMode: LineChangeMode,
) : LiveDto() {
    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     */
    override fun encrypt() = copy(lineContent = encrypt(lineContent))

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     */
    override fun decrypt() = copy(lineContent = decrypt(lineContent))
}
