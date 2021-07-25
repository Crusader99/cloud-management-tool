package de.hsaalen.cmt.network.dto.websocket

import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.crypto.encrypt
import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.serialization.Serializable

/**
 * Data transfer object to describe label add or removal.
 */
@Serializable
data class LabelUpdateDto(
    val reference: UUID,
    val labelName: String,
    val mode: LabelChangeMode
) : LiveDto() {
    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     */
    override fun encrypt() = copy(labelName = encrypt(labelName))

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     */
    override fun decrypt() = copy(labelName = decrypt(labelName))
}
