package de.hsaalen.cmt.network.session

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.network.requests.RequestAuthentication
import de.hsaalen.cmt.network.requests.RequestCreateReferences
import de.hsaalen.cmt.network.requests.RequestDownload
import de.hsaalen.cmt.network.requests.RequestListReferences
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias Listener = (DocumentChangeDto) -> Unit

/**
 * Client session with websocket connection to server backend.
 */
class Session(val userInfo: ServerUserInfoDto) :
    RequestListReferences,
    RequestCreateReferences,
    RequestDownload {

    // True while the websocket is connected to the server
    var isConnected = true
        private set

    private var webSocketSendingQueue = Channel<Frame>()
    private val webSocketListeners = mutableListOf<Listener>()

    init {
        GlobalScope.launch {
            connectWebSocket()
        }
    }

    /**
     * Add listener for handling received DTOs.
     */
    fun registerListener(packetListener: Listener) {
        webSocketListeners += packetListener
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
        try {
            Client.instance.ws(urlString = url) {
                println("Connected websocket")

                launch {
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
                    val dto: DocumentChangeDto = if (frame is Frame.Text) {
                        val jsonText = frame.readText()
                        Json.decodeFromString(jsonText)
                    } else {
                        throw UnsupportedOperationException(frame::class.simpleName)
                    }
                    for (handler in webSocketListeners) {
                        if (isConnected) {
                            handler(dto)
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

    /**
     * Send to text edit DTO to server and other clients.
     */
    suspend fun liveTextEdit(dto: DocumentChangeDto) {
        val jsonText = Json.encodeToString(dto)
        webSocketSendingQueue.send(Frame.Text(jsonText))
    }

    /**
     * Disconnect the websocket from server
     */
    suspend fun logout() {
        isConnected = false
        try {
            RequestAuthentication.logout()
        } catch (t: Exception) {
            // Ignore any errors
        }
    }

    companion object {
        /**
         * The current session instance.
         */
        var instance: Session? = null

        /**
         * Send login request to the server.
         */
        suspend fun login(email: String, passwordPlain: String): Session {
            val passwordHashed = passwordPlain // TODO: hash password
            val userInfo = RequestAuthentication.login(email, passwordHashed)
            println("Logged in as: " + userInfo.email)
            return Session(userInfo)
        }

        /**
         * Send register request to the server.
         */
        suspend fun register(firstName: String, email: String, passwordPlain: String): Session {
            val passwordHashed = passwordPlain // TODO: hash password
            val userInfo = RequestAuthentication.register(firstName, email, passwordHashed)
            println("Logged in as: " + userInfo.email)
            return Session(userInfo)
        }

        /**
         * Request server to restore user session. Session can only restored when JWT cookie is still valid.
         *
         * @return Session instance when session has been restored or null when it can't be restored.
         * @throws ConnectException when no connection to backend de.hsaalen.cmt.services was possible.
         */
        suspend fun restore(): Session? {
            return try {
                val userInfo = RequestAuthentication.restore()
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
