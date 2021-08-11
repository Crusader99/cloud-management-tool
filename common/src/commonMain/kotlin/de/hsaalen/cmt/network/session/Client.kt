package de.hsaalen.cmt.network.session

import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.network.dto.rsocket.LiveDto
import de.hsaalen.cmt.network.dto.server.ServerErrorDto
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.network.exceptions.ServerException
import de.hsaalen.cmt.network.exceptions.UnauthorizedException
import de.hsaalen.cmt.utils.decodeProtobufData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.keepalive.KeepAlive
import io.rsocket.kotlin.payload.PayloadMimeType
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import io.rsocket.kotlin.transport.ktor.client.RSocketSupport
import kotlinx.coroutines.withTimeout
import mu.KotlinLogging

/**
 * Client library for web-app and android-app.
 */
internal object Client {

    /**
     * Logging instance for this class.
     */
    private val logger = KotlinLogging.logger("network-client")

    /**
     * Actual network client from ktor with multi platform support.
     */
    val instance: HttpClient = configure()

    /**
     * Allow reconfiguring the client for a specific user info to provide correct JWT token.
     */
    fun configure(userInfo: ServerUserInfoDto? = null) = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
        }
        if (userInfo != null) { // Only enable rSocket support when user info provided
            install(WebSockets) // Required to use rSocket over websocket
            install(RSocketSupport) {
                connector = RSocketConnector {
                    // Configure rSocket connector (all values have defaults)
                    connectionConfig {
                        keepAlive = KeepAlive(
                            intervalMillis = 5_000,
                            maxLifetimeMillis = 25_000
                        )

                        // Payload for setup frame
                        setupPayload { buildPayload { data(userInfo.jwtToken) } }

                        // Mime types
                        payloadMimeType = PayloadMimeType(
                            data = "application/json",
                            metadata = "application/json"
                        )
                    }

                    // Acceptor for server requests
                    acceptor {
                        RSocketRequestHandler {
                            fireAndForget { payload ->
                                try {
                                    logger.info { "Received encrypted DTO from server over RSocket" }
                                    val dto: LiveDto = payload.decodeProtobufData()
                                    logger.info { "Decrypt DTO and dispatch event: " + dto::class.simpleName }
                                    GlobalEventDispatcher.notify(dto.decrypt())
                                } catch (t: Throwable) {
                                    logger.error(t) { "Unable to handle received DTO" }
                                }
                            }
                        }
                    }
                }
            }
        }
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
            logger.info { "Server-Error ($statusCode): $statusDescription: $errorMessage" }
            throw ServerException(statusCode, errorMessage)
        } catch (t: Throwable) {
            val message = t.message.takeIf { it?.length in 3..100 } ?: "Server-Error ($statusCode)"
            when (response.status) {
                HttpStatusCode.BadGateway -> throw ConnectException(message, t)
                HttpStatusCode.GatewayTimeout -> throw ConnectException(message, t)
                HttpStatusCode.Unauthorized -> throw UnauthorizedException(message, t)
                else -> throw ServerException(statusCode, message, t)
            }
        }
    }

}
