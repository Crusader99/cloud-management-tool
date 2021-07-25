package de.hsaalen.cmt.network.dto.server

import de.hsaalen.cmt.crypto.Encryptable
import de.hsaalen.cmt.network.dto.objects.Reference
import kotlinx.serialization.Serializable

/**
 * Data transfer object to provide a list of all related references.
 */
@Serializable
data class ServerReferenceListDto(
    val references: List<Reference>
) : ServerDto, Encryptable<ServerReferenceListDto> {
    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     */
    override fun encrypt() = copy(references = references.map { it.encrypt() })

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     */
    override fun decrypt() = copy(references = references.map { it.decrypt() })
}
