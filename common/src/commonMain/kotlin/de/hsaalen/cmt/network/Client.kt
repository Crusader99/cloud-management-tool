package de.hsaalen.cmt.network

import de.hsaalen.cmt.network.dto.AuthLoginDto
import de.hsaalen.cmt.network.dto.AuthResultDto
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Client library for web-app and android-app.
 */
object Client {

    // Actual network client from ktor with multi platform support
    private val instance = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
    }

    // The url to use for requests to REST API server
    var apiEndpoint = RestPaths.base

    /**
     * Send login request to the server.
     */
    suspend fun login(username: String, password: String) {
        val passwordHashed = password // TODO: hash password
        val request = AuthLoginDto(username, passwordHashed)
        val url = Url("$apiEndpoint/login")
        val result: AuthResultDto = instance.post(url)
        println(result.message)
    }

}