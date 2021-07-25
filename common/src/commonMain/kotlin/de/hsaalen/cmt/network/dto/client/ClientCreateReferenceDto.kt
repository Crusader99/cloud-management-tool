package de.hsaalen.cmt.network.dto.client

import de.hsaalen.cmt.crypto.Encryptable
import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.crypto.encrypt
import kotlinx.serialization.Serializable

/**
 * Data transfer object to describe a detailed search query to request related references.
 */
@Serializable
data class ClientCreateReferenceDto(
    val displayName: String,
    val content: String = "",
    val labels: List<String> = emptyList(),
) : ClientDto, Encryptable<ClientCreateReferenceDto> {
    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     */
    override fun encrypt() = copy(content = encrypt(content), labels = labels.encrypt())

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     */
    override fun decrypt() = copy(content = decrypt(content), labels = labels.decrypt())
}
