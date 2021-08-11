package de.hsaalen.cmt.network.dto.client

import de.hsaalen.cmt.crypto.Encryptable
import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.crypto.encrypt
import kotlinx.serialization.Serializable

/**
 * Data transfer object to describe a detailed search query to request related references.
 */
@Serializable
data class ClientReferenceQueryDto(
    val searchName: String = "",
    val filterLabels: Set<String> = emptySet()
) : ClientDto, Encryptable<ClientReferenceQueryDto> {

    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     * Note that for labels the secureRandomizedPadding is disabled because encrypted data has to be the same every time.
     * This is required because of features like the search by label function.
     * Also, the search name keeps unencrypted to allow a contains check.
     */
    override fun encrypt() = copy(filterLabels = filterLabels.encrypt(secureRandomizedPadding = false).toSet())

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     * Note that for labels the secureRandomizedPadding is disabled because encrypted data has to be the same every time.
     * This is required because of features like the search by label function.
     * Also, the search name keeps unencrypted to allow a contains check.
     */
    override fun decrypt() = copy(filterLabels = filterLabels.decrypt(secureRandomizedPadding = false).toSet())

}
