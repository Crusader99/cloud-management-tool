package de.hsaalen.cmt.test.rsocket

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.rsocket.kotlin.RSocketRequestHandler
import io.rsocket.kotlin.core.RSocketConnector
import io.rsocket.kotlin.core.RSocketServer
import io.rsocket.kotlin.keepalive.KeepAlive
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.PayloadMimeType
import io.rsocket.kotlin.payload.buildPayload
import io.rsocket.kotlin.payload.data
import io.rsocket.kotlin.transport.ktor.client.RSocketSupport
import io.rsocket.kotlin.transport.ktor.client.rSocket
import io.rsocket.kotlin.transport.ktor.server.rSocket
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import java.net.ServerSocket
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for experimenting with RSocket framework and ensure RSocket is working as expected.
 * Based on examples from {https://github.com/rsocket/rsocket-kotlin}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RSocketTest {

    /**
     * Detect free port on host system.
     */
    private val freePort = ServerSocket(0).use { it.localPort }

    /**
     * Amount of messages that should be transmitted from server to client.
     */
    private val messageCount = 100;

    /**
     * Setup RSocket server that extends web-socket functionality.
     */
    @BeforeTest
    fun setupServer() {
        embeddedServer(CIO, port = freePort) {
            install(io.ktor.websocket.WebSockets)
            install(io.rsocket.kotlin.transport.ktor.server.RSocketSupport) {
                this.server = RSocketServer {}
            }
            routing {
                // Configure route "url:port/rsocket"
                rSocket("rsocket") {
                    RSocketRequestHandler {
                        requestStream {
                            flow {
                                repeat(messageCount) { i ->
                                    emit(buildPayload { data("data: $i") })
                                }
                            }
                        }
                    }
                }
            }
        }.start(false)
    }

    /**
     * Create RSocket client connection test connection with server.
     */
    @Test
    fun testClient() {
        // Create and configure new ktor client
        val client = HttpClient {
            install(WebSockets)
            install(RSocketSupport) {
                connector = RSocketConnector {
                    // Configure rSocket connector (all values have defaults)
                    connectionConfig {
                        keepAlive = KeepAlive(
                            intervalMillis = 30_000,
                            maxLifetimeMillis = 2_000
                        )

                        // Define setup frame payload
                        setupPayload { buildPayload { data("test") } }

                        // Configure mime types
                        payloadMimeType = PayloadMimeType(
                            data = "application/json",
                            metadata = "application/json"
                        )
                    }
                }
            }
        }

        // Connect to server
        runBlocking {
            val rsocket = client.rSocket("ws://localhost:$freePort/rsocket")
            var receivedMessages = 0
            rsocket.requestStream(Payload.Empty).collect { payload ->
                println(payload.data.readText())
                receivedMessages++
            }
            assertEquals(messageCount, receivedMessages, "Unexpected amount of received messages")
        }
    }

}
