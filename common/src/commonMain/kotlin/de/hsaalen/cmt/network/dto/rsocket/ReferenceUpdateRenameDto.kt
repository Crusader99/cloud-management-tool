package de.hsaalen.cmt.network.dto.rsocket

import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.serialization.Serializable

/**
 * Data transfer object when any reference has been renamed.
 */
@Serializable
data class ReferenceUpdateRenameDto(
    val uuid: UUID,
    val newName: String
) : ReferenceUpdateDto() {

    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     */
    override fun encrypt() = this

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     */
    override fun decrypt() = this

}
