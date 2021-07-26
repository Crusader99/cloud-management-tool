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
    val contentType: ContentType,
    val dateCreation: Long,
    val dateLastAccess: Long,
    val labels: MutableSet<String>
) : Encryptable<Reference> {

    /**
     * Encrypt sensible information using personal session key and return new encrypted instance.
     * Note that for labels the secureRandomizedPadding is disabled because encrypted data has to be the same every time.
     * This is required because of features like the search by label function.
     */
    override fun encrypt() = copy(labels = labels.encrypt(secureRandomizedPadding = false).toMutableSet())

    /**
     * Decrypt sensible information using personal session key and return new decrypted instance.
     * Note that for labels the secureRandomizedPadding is disabled because encrypted data has to be the same every time.
     * This is required because of features like the search by label function.
     */
    override fun decrypt() = copy(labels = labels.decrypt(secureRandomizedPadding = false).toMutableSet())

}
