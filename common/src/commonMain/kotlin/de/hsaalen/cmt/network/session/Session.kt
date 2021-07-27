package de.hsaalen.cmt.network.session

import com.soywiz.krypto.encoding.fromBase64
import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathRSocket
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.network.dto.websocket.LiveDto
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.network.requests.AuthenticationRepositoryImpl
import de.hsaalen.cmt.network.requests.DocumentRepositoryImpl
import de.hsaalen.cmt.network.requests.LabelRepositoryImpl
import de.hsaalen.cmt.network.requests.ReferenceRepositoryImpl
import de.hsaalen.cmt.repository.AuthenticationRepository
import de.hsaalen.cmt.utils.protobufData
import io.ktor.client.*
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.transport.ktor.client.rSocket
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.withTimeout
import mu.KotlinLogging

/**
 * Client session with websocket connection to server backend.
 */
class Session(val userInfo: ServerUserInfoDto) : ReferenceRepositoryImpl, LabelRepositoryImpl, DocumentRepositoryImpl {

    /**
     * True while the websocket is connected to the server.
     */
    val isConnected
        get() = rSocket != null && rSocket?.job?.isActive == true

    /**
     * Session related http client for RSocket request.
     */
    private var client: HttpClient = Client.configure(userInfo)

    /**
     * Current rSocket connection to server.
     *
     * @see {https://rsocket.io}
     */
    private var rSocket: RSocket? = null

    /**
     * Local logger instance for this specific user [Session].
     */
    private val logger = KotlinLogging.logger(this::class.simpleName + "|" + userInfo.email)

    /**
     * Connect to server with websocket for live synchronization.
     */
    private suspend fun connectWebSocket() {
        try {
            var url = "${RestPaths.apiEndpoint}$apiPathRSocket"
            if (url.startsWith("http")) {
                // Replace http protocol with ws protocol
                // Should also work with tls encryption
                url = "ws" + url.removePrefix("http")
            }
            this.rSocket = client.rSocket(url)
            logger.info { "Connected RSocket over websocket" }
        } catch (ex: Exception) {
            throw IllegalStateException("Unable to establish RSocket connection", ex)
        }
    }

    /**
     * Send [LiveDto] over rSocket connection.
     */
    suspend fun sendLiveDTO(dto: LiveDto) {
        val payload = buildPayload {
            // Encrypt content before sending to server
            protobufData(dto.encrypt())
        }
        rSocket?.fireAndForget(payload)
    }

    /**
     * Disconnect the websocket from server
     */
    suspend fun logout() {
        if (instance == null) {
            // Ignore because already session cancelled
            // This can't cause any concurrent issues due to single-context suspend function
            return
        }
        instance = null
        rSocket = null
        try {
            withTimeout(2_000) {
                rSocket?.job?.cancelAndJoin()
                client.close()
            }
        } catch (t: Throwable) {
            // Ignore any errors
            logger.error(t) { "Unable to close clients of session" }
        }
        try {
            authRepo.logout()
        } catch (t: Throwable) {
            // Ignore any errors
            logger.error(t) { "Unable to perform logout" }
        }
        logger.info { "Closed session" }
    }

    companion object {
        /**
         * The current session instance.
         */
        var instance: Session? = null
            private set

        /**
         * Personal master crypto key that can be used for encrypting and decrypting all non reference related data.
         */
        // TODO: decrypt key before using. a key for decryption the key is required. store in cookie?
        val personalKey: ByteArray
            get() = instance?.userInfo?.personalKeyBase64?.fromBase64() ?: error("No key available")

        /**
         * Factory for [AuthenticationRepository] implementation.
         */
        private val authRepo: AuthenticationRepository
            get() = AuthenticationRepositoryImpl

        /**
         * Send login request to the server.
         */
        suspend fun login(email: String, passwordPlain: String) {
            val userInfo = authRepo.login(email, passwordPlain)
            instance = Session(userInfo).apply {
                connectWebSocket()
                logger.info { "Logged in as: " + userInfo.fullName }
            }
        }

        /**
         * Send register request to the server.
         */
        suspend fun register(firstName: String, email: String, passwordPlain: String) {
            val userInfo = authRepo.register(firstName, email, passwordPlain)
            instance = Session(userInfo).apply {
                connectWebSocket()
                logger.info { "Registered as: " + userInfo.fullName }
            }
        }

        /**
         * Request server to restore user session. Session can only restored when JWT cookie is still valid.
         *
         * @return True when session has been restored or false when it can't be restored.
         * @throws ConnectException when no connection to backend services was possible.
         */
        suspend fun restore(): Boolean {
            return try {
                val userInfo = authRepo.restore() // No email passed: not known yet, determined by server
                instance = Session(userInfo).apply {
                    connectWebSocket()
                    logger.info { "Restored session for: " + userInfo.fullName }
                }
                true // Operation was successful
            } catch (t: Exception) {
                // Return null when restore not accepted
                // But throw exception when connect failed
                if (t is ConnectException) throw t else false
            }
        }

    }

}

