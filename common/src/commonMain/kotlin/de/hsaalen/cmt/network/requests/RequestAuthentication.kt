package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.apiPathAuthLogin
import de.hsaalen.cmt.network.apiPathAuthLogout
import de.hsaalen.cmt.network.apiPathAuthRegister
import de.hsaalen.cmt.network.apiPathAuthRestore
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.network.session.Client
import io.ktor.http.*

/**
 * Collection of all HTTP requests used by web-app and android-app.
 * Provides full multi-platform support by using ktor clients.
 */
internal object RequestAuthentication : Request {

    /**
     * Send register request to the server.
     */
    suspend fun register(
        firstName: String,
        email: String,
        passwordHashed: String
    ): ServerUserInfoDto = Client.request(Url("$apiEndpoint$apiPathAuthRegister")) {
        method = HttpMethod.Post
        body = ClientRegisterDto(firstName, email, passwordHashed)
    }

    /**
     * Send login request to the server.
     */
    suspend fun login(email: String, passwordHashed: String): ServerUserInfoDto {
        val url = Url("$apiEndpoint$apiPathAuthLogin")
        return Client.request(url) {
            method = HttpMethod.Post
            body = ClientLoginDto(email, passwordHashed)
        }
    }

    /**
     * Sends logout request to server to receive a HTTP header to get cookies deleted.
     */
    suspend fun logout(): Unit = Client.request(Url("$apiEndpoint$apiPathAuthLogout")) {
        method = HttpMethod.Post
    }

    /**
     * Send request to the server for restoring user session. Session can only restored when JWT
     * cookie is still valid.
     */
    suspend fun restore(): ServerUserInfoDto = Client.request(Url("$apiEndpoint$apiPathAuthRestore")) {
        method = HttpMethod.Get
    }

}
