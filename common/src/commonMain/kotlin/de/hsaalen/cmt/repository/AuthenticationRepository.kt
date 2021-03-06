package de.hsaalen.cmt.repository

import de.hsaalen.cmt.crypto.generateCryptoKey
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto

/**
 * Repository port for providing user authentication infrastructure. This can be implemented by the server for SQL database
 * access or implemented for the client to access the server over network. The implementation can be injected using
 * dependency injection.
 */
interface AuthenticationRepository {

    /**
     * Send register request to the server.
     */
    suspend fun register(
        fullName: String,
        email: String,
        passwordPlain: String,
        personalKey: ByteArray = generateCryptoKey()
    ): ServerUserInfoDto

    /**
     * Send login request to the server.
     */
    suspend fun login(email: String, passwordPlain: String): ServerUserInfoDto

    /**
     * Sends logout request to server to receive an HTTP header to get cookies deleted.
     */
    suspend fun logout()

    /**
     * Send request to the server for restoring user session. Session is only restored when JWT
     * cookie is still valid.
     */
    suspend fun restore(): ServerUserInfoDto

}
