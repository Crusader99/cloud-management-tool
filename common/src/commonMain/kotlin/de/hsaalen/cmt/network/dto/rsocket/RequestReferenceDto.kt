package de.hsaalen.cmt.network.dto.rsocket

import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.serialization.Serializable

/**
 * Request server to provide content to a specific reference.
 */
@Serializable
data class RequestReferenceDto(
    val reference: UUID,
) : ReferenceUpdateDto() {

    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     */
    override fun encrypt() = this // No sensible information here

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     */
    override fun decrypt() = this // No sensible information here

}
