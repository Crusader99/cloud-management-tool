package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.apiPathAuthLogin
import de.hsaalen.cmt.network.apiPathAuthLogout
import de.hsaalen.cmt.network.apiPathAuthRegister
import de.hsaalen.cmt.network.apiPathAuthRestore
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.network.session.Client
import de.hsaalen.cmt.repository.AuthenticationRepository
import io.ktor.http.*

/**
 * Collection of all HTTP requests used by web-app and android-app.
 * Provides full multi-platform support by using ktor clients.
 */
internal object RequestAuthentication : Request, AuthenticationRepository {

    /**
     * Send register request to the server.
     */
    override suspend fun register(
        fullName: String,
        email: String,
        passwordPlain: String
    ): ServerUserInfoDto {
        val passwordHashed = passwordPlain // TODO: hash password
        val url = Url("$apiEndpoint$apiPathAuthRegister")
        return Client.request(url) {
            method = HttpMethod.Post
            body = ClientRegisterDto(fullName, email, passwordHashed)
        }
    }

    /**
     * Send login request to the server.
     */
    override suspend fun login(email: String, passwordPlain: String): ServerUserInfoDto {
        val passwordHashed = passwordPlain // TODO: hash password
        val url = Url("$apiEndpoint$apiPathAuthLogin")
        return Client.request(url) {
            method = HttpMethod.Post
            body = ClientLoginDto(email, passwordHashed)
        }
    }

    /**
     * Sends logout request to server to receive a HTTP header to get cookies deleted.
     */
    override suspend fun logout(): Unit = Client.request(Url("$apiEndpoint$apiPathAuthLogout")) {
        method = HttpMethod.Post
    }

    /**
     * Send request to the server for restoring user session. Session can only restored when JWT
     * cookie is still valid.
     *
     * Note: Email parameter may be empty in this implementation because it is determined from server by cookie.
     */
    override suspend fun restore(email: String): ServerUserInfoDto {
        val url = Url("$apiEndpoint$apiPathAuthRestore")
        return Client.request(url) {
            method = HttpMethod.Get
        }
    }

}
