package de.hsaalen.cmt.network.session

import de.hsaalen.cmt.network.dto.server.ServerErrorDto
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.network.exceptions.ServerException
import de.hsaalen.cmt.network.exceptions.UnauthorizedException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.withTimeout

/**
 * Client library for web-app and android-app.
 */
internal object Client {

    /**
     * Actual network client from ktor with multi platform support.
     */
    val instance = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        install(WebSockets)
    }

    /**
     * Network request method for all types of HTTP requests. This function handles functionality that is used in
     * multiple requests.
     *
     * Provided functionality:
     * - Defined timeout for requests
     * - Configures JSON content type
     * - Handles all types exceptions (also parses error message from json)
     */
    suspend inline fun <reified RECEIVE> request(
        url: Url,
        json: Boolean = true,
        timeout: Long = 5_000,
        crossinline configure: HttpRequestBuilder.() -> Unit = {}
    ): RECEIVE {
        val response: HttpResponse = try {
            withTimeout(timeout) {
                instance.request(url) {
                    if (json) {
                        header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                    configure()
                    expectSuccess = false
                }
            }
        } catch (t: Throwable) {
            val message = "Request failed"
            val cause = t.message
            if (cause == null) {
                throw ConnectException(message, t)
            } else {
                throw ConnectException("$message: $cause", t)
            }
        }
        try {
            if (response.status.isSuccess()) {
                return response.receive()
            }
        } catch (t: Throwable) {
            throw IllegalStateException("Connected but JSON wrong", t)
        }
        val statusCode = response.status.value
        val statusDescription = response.status.description
        try {
            val errorInfo: ServerErrorDto = response.receive()
            val errorMessage = errorInfo.error
            println("Server-Error ($statusCode): $statusDescription: $errorMessage")
            throw ServerException(statusCode, errorMessage)
        } catch (t: Throwable) {
            val message = "Server-Error ($statusCode)"
            println(message)
            when (response.status) {
                HttpStatusCode.GatewayTimeout -> throw ConnectException(message, t)
                HttpStatusCode.Unauthorized -> throw UnauthorizedException(message, t)
                else -> throw ServerException(statusCode, message, t)
            }
        }
    }

}
