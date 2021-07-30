package de.hsaalen.cmt.network.dto.rsocket

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
     * Note that for labels the secureRandomizedPadding is disabled because encrypted data has to be the same every time.
     * This is required because of features like the search by label function.
     */
    override fun encrypt() = copy(labelName = encrypt(labelName, secureRandomizedPadding = false))

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     * Note that for labels the secureRandomizedPadding is disabled because encrypted data has to be the same every time.
     * This is required because of features like the search by label function.
     */
    override fun decrypt() = copy(labelName = decrypt(labelName, secureRandomizedPadding = false))

}
