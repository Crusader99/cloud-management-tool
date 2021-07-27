package de.hsaalen.cmt.network.dto.client

import de.hsaalen.cmt.crypto.Encryptable
import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.crypto.encrypt
import de.hsaalen.cmt.network.dto.objects.ContentType
import kotlinx.serialization.Serializable

/**
 * Data transfer object to describe a detailed search query to request related references.
 */
@Serializable
data class ClientCreateReferenceDto(
    val displayName: String,
    val contentType: ContentType,
    val documentLines: List<String> = emptyList(),
    val labels: Set<String> = emptySet(),
) : ClientDto, Encryptable<ClientCreateReferenceDto> {

    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     * Note that for labels the secureRandomizedPadding is disabled because encrypted data has to be the same every time.
     * This is required because of features like the search by label function.
     */
    override fun encrypt() =
        copy(documentLines = documentLines.encrypt(), labels = labels.encrypt(secureRandomizedPadding = false).toSet())

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     * Note that for labels the secureRandomizedPadding is disabled because encrypted data has to be the same every time.
     * This is required because of features like the search by label function.
     */
    override fun decrypt() =
        copy(documentLines = documentLines.decrypt(), labels = labels.decrypt(secureRandomizedPadding = false).toSet())
}
