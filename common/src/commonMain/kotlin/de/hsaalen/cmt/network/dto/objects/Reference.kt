package de.hsaalen.cmt.network.dto.objects

import de.hsaalen.cmt.crypto.Encryptable
import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.crypto.encrypt
import kotlinx.serialization.Serializable

/**
 * A single reference object that can be serialized and transferred over network.
 */
@Serializable
data class Reference(
    val uuid: UUID,
    val accessCode: String?,
    val displayName: String,
    val contentType: String,
    val dateCreation: Long,
    val dateLastAccess: Long,
    val labels: List<String>
) : Encryptable<Reference> {
    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     */
    override fun encrypt() = copy(labels = labels.encrypt())

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     */
    override fun decrypt() = copy(labels = labels.decrypt())
}
