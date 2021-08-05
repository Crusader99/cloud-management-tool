package de.hsaalen.cmt.network.dto.server

import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.crypto.encrypt
import de.hsaalen.cmt.crypto.fromBase64
import de.hsaalen.cmt.crypto.toBase64
import kotlinx.serialization.Serializable

/**
 * Data transfer object from server for the result of client's login request.
 */
@Serializable
data class ServerUserInfoDto(
    val fullName: String,
    val email: String,
    val personalKeyBase64: String,
    var jwtToken: String = "" // Will be injected before sending over network
) : ServerDto {

    /**
     * Encrypt personal private using plain password and return new encrypted instance.
     */
    fun encrypt(plainPassword: String) =
        copy(personalKeyBase64 = encrypt(personalKeyBase64.fromBase64(), plainPassword.encodeToByteArray()).toBase64())

    /**
     * Decrypt personal private key using plain password and return new decrypted instance.
     */
    fun decrypt(plainPassword: String) =
        copy(personalKeyBase64 = decrypt(personalKeyBase64.fromBase64(), plainPassword.encodeToByteArray()).toBase64())

}
