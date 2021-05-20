package de.hsaalen.cmt.network.client

import de.hsaalen.cmt.network.dto.server.ServerErrorDto
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.network.exceptions.ServerException
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
        crossinline configure: HttpRequestBuilder.() -> Unit = {}
    ): RECEIVE {
        val response: HttpResponse = try {
            withTimeout(5_000L) {
                instance.request(url) {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
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
            val errorMessage = errorInfo.message
            println("Server-Error ($statusCode): $statusDescription: $errorMessage")
            throw ServerException(statusCode, errorMessage)
        } catch (t: Throwable) {
            println("Server-Error ($statusCode)")
            throw ServerException(statusCode, "Server-Error ($statusCode)", t)
        }
    }

}
