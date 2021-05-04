package de.hsaalen.cmt.network

import de.hsaalen.cmt.network.dto.AuthLoginDto
import de.hsaalen.cmt.network.dto.AuthResultDto
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Client library for web-app and android-app.
 */
class Client {

    // True while the websocket is connected to the server
    var isConnected = true

    var webSocketSendingQueue = Channel<Frame>()
    var webSocketReceiveHandlers = mutableListOf<(Frame) -> Unit>()

    /**
     * Disconnect the websocket from server
     */
    fun disconnect() {
        isConnected = false
    }

    /**
     * Connect to server with websocket for live synchronization.
     */
    private suspend fun connectWebSocket() {
        var url = "$apiEndpoint/websocket"
        if (url.startsWith("http")) {
            // Replace http protocol with ws protocol
            // Should also work with tls encryption
            url = "ws" + url.removePrefix("http")
        }
        GlobalScope.launch {
            try {
                instance.ws(urlString = url) {
                    println("Connected websocket")
                    send("test with text frame")
                    send(Frame.Text("custom fame"))

                    GlobalScope.launch {
                        try {
                            for (frame in webSocketSendingQueue) {
                                if (isActive && isConnected) {
                                    send(frame)
                                } else {
                                    throw IllegalStateException("No connected")
                                }
                            }
                        } catch (ex: Throwable) {
                            disconnect()
                        }
                    }

                    while (isActive && isConnected) {
                        println("Waiting for websocket receive")
                        val frame = incoming.receive()
                        for (handler in webSocketReceiveHandlers) {
                            handler(frame)
                        }
//                        when (val frame = incoming.receive()) {
//                            is Frame.Text -> println(frame.readText())
//                            is Frame.Binary -> println(frame.readBytes())
//                            else -> println("Unknown frame: " + frame.frameType.name)
//                        }
                    }
                    println("Finished websocket")
                }

            } finally {
                disconnect()
            }
        }
    }

    companion object {
        // Actual network client from ktor with multi platform support
        private val instance = HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
            install(WebSockets)
        }

        // The url to use for requests to REST API server
        var apiEndpoint = RestPaths.base

        /**
         * Send login request to the server.
         */
        suspend fun login(username: String, password: String): Client {
            val passwordHashed = password // TODO: hash password
            val request = AuthLoginDto(username, passwordHashed)
            val url = Url("$apiEndpoint/login")
            val result: AuthResultDto = instance.post(url)
            println(result.message)
            val client = Client()
            client.connectWebSocket()
            return client
        }
    }

}