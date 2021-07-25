package de.hsaalen.cmt.network.dto.websocket

import de.hsaalen.cmt.network.dto.objects.Reference
import kotlinx.serialization.Serializable

/**
 * Data transfer object when any reference has been added.
 */
@Serializable
data class ReferenceUpdateAddDto(
    val reference: Reference,
) : ReferenceUpdateDto() {

    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     */
    override fun encrypt() = copy(reference = reference.encrypt())

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     */
    override fun decrypt() = copy(reference = reference.decrypt())
}
