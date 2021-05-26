package de.hsaalen.cmt.network.client

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.network.exceptions.ConnectException
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Client session with websocket connection to server backend.
 */
class Session(val userInfo: ServerUserInfoDto) {

    // True while the websocket is connected to the server
    var isConnected = true

    var webSocketSendingQueue = Channel<Frame>()
    var webSocketReceiveHandlers = mutableListOf<(Frame) -> Unit>()

    init {
        GlobalScope.launch {
            connectWebSocket()
        }
    }

    /**
     * Connect to server with websocket for live synchronization.
     */
    private suspend fun connectWebSocket() {
        var url = "${RestPaths.base}/websocket"
        if (url.startsWith("http")) {
            // Replace http protocol with ws protocol
            // Should also work with tls encryption
            url = "ws" + url.removePrefix("http")
        }
        GlobalScope.launch {
            try {
                Client.instance.ws(urlString = url) {
                    println("Connected websocket")
                    send("test with text frame")
                    send(Frame.Text("custom fame"))

                    GlobalScope.launch {
                        try {
                            for (frame in webSocketSendingQueue) {
                                if (isActive && isConnected) {
                                    send(frame)
                                } else {
                                    throw IllegalStateException("Not connected with websocket")
                                }
                            }
                        } catch (ex: Throwable) {
                            isConnected = false
                        }
                    }

                    while (isActive && isConnected) {
                        println("Waiting for websocket receive")
                        val frame = incoming.receive()
                        for (handler in webSocketReceiveHandlers) {
                            if (isConnected) {
                                handler(frame)
                            } else {
                                throw IllegalStateException("Not connected with websocket")
                            }
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
                isConnected = false
            }
        }
    }

    /**
     * Provide a list of all related references.
     */
    suspend fun listReferences() = Requests.listReferences()

    /**
     * Disconnect the websocket from server
     */
    suspend fun logout() {
        isConnected = false
        try {
            Requests.logout()
        } catch (t: Exception) {
            // Ignore any errors
        }
    }

    companion object {
        /**
         * Send login request to the server.
         */
        suspend fun login(email: String, passwordPlain: String): Session {
            val passwordHashed = passwordPlain // TODO: hash password
            val userInfo = Requests.login(email, passwordHashed)
            println("Logged in as: " + userInfo.email)
            return Session(userInfo)
        }

        /**
         * Send register request to the server.
         */
        suspend fun register(firstName: String, email: String, passwordPlain: String): Session {
            val passwordHashed = passwordPlain // TODO: hash password
            val userInfo = Requests.register(firstName, email, passwordHashed)
            println("Logged in as: " + userInfo.email)
            return Session(userInfo)
        }

        /**
         * Request server to restore user session. Session can only restored when JWT cookie is still valid.
         *
         * @return Session instance when session has been restored or null when it can't be restored.
         * @throws ConnectException when no connection to backend services was possible.
         */
        suspend fun restore(): Session? {
            return try {
                val userInfo = Requests.restore()
                println("Restored session for: " + userInfo.email)
                Session(userInfo)
            } catch (t: Exception) {
                // Return null when restore not accepted
                // But throw exception when connect failed
                if (t is ConnectException) throw t else null
            }
        }

    }
}
