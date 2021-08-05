package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.crypto.encrypt
import de.hsaalen.cmt.crypto.hashSHA256
import de.hsaalen.cmt.crypto.toBase64
import de.hsaalen.cmt.network.apiPathAuthLogin
import de.hsaalen.cmt.network.apiPathAuthLogout
import de.hsaalen.cmt.network.apiPathAuthRegister
import de.hsaalen.cmt.network.apiPathAuthRestore
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.network.session.Client
import de.hsaalen.cmt.network.session.PersonalKeyManagement
import de.hsaalen.cmt.repository.AuthenticationRepository
import de.hsaalen.cmt.utils.ClientSupport
import io.ktor.http.*

/**
 * Collection of all HTTP requests used by web-app and android-app.
 * Provides full multi-platform support by using ktor clients.
 */
internal object AuthenticationRepositoryImpl : ClientSupport, AuthenticationRepository {

    /**
     * Send register request to the server.
     */
    override suspend fun register(
        fullName: String,
        email: String,
        passwordPlain: String,
        personalKey: ByteArray
    ): ServerUserInfoDto {
        val encryptedPersonalKey = encrypt(personalKey, passwordPlain.encodeToByteArray()).toBase64()
        val passwordHashed = hashPassword(passwordPlain)
        val url = Url("$apiEndpoint$apiPathAuthRegister")
        val encryptedInfo: ServerUserInfoDto = Client.request(url) {
            method = HttpMethod.Post
            body = ClientRegisterDto(fullName, email, passwordHashed, encryptedPersonalKey)
        }
        val decryptedInfo: ServerUserInfoDto = encryptedInfo.decrypt(passwordPlain)
        PersonalKeyManagement.store(decryptedInfo)
        return decryptedInfo
    }

    /**
     * Send login request to the server.
     */
    override suspend fun login(email: String, passwordPlain: String): ServerUserInfoDto {
        val passwordHashed = hashPassword(passwordPlain)
        val url = Url("$apiEndpoint$apiPathAuthLogin")
        val encryptedInfo: ServerUserInfoDto = Client.request(url) {
            method = HttpMethod.Post
            body = ClientLoginDto(email, passwordHashed)
        }
        val decryptedInfo: ServerUserInfoDto = encryptedInfo.decrypt(passwordPlain)
        PersonalKeyManagement.store(decryptedInfo)
        return decryptedInfo
    }

    /**
     * Sends logout request to server to receive an HTTP header to get cookies deleted.
     */
    override suspend fun logout(): Unit = try { // Define type explicit to ignore result
        Client.request(Url("$apiEndpoint$apiPathAuthLogout")) {
            method = HttpMethod.Post
        }
    } finally {
        PersonalKeyManagement.delete()
    }

    /**
     * Send request to the server for restoring user session. Session is only restored when JWT
     * cookie is still valid.
     */
    override suspend fun restore(): ServerUserInfoDto {
        val url = Url("$apiEndpoint$apiPathAuthRestore")
        val info: ServerUserInfoDto = Client.request(url) {
            method = HttpMethod.Get
        }
        PersonalKeyManagement.load(info.email) ?: error("No key available")
        return info
    }

    /**
     * Hash the given password parameter WITHOUT salting.
     *
     * Note: Salting only happens on server side and a specific salt is defined by setting an environment variable.
     */
    private fun hashPassword(password: String): String {
        // Hash password without salting on clientside and convert to hex string
        // Before saving password in database the server will hash the password again with a salt
        return hashSHA256(password)
    }

}
