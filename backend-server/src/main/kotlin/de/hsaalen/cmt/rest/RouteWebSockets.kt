package de.hsaalen.cmt.rest

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathWebSocket
import de.hsaalen.cmt.websocket.Connection
import de.hsaalen.cmt.websocket.WebSocketManager
import io.ktor.auth.*
import io.ktor.routing.*
import io.ktor.websocket.*


/**
 * Handler for web-sockets that are used for live synchronisation of cloud data, e.g. file edit.
 */
fun Route.routeWebSockets() = route("/" + RestPaths.base) {
    authenticate {
        webSocket(apiPathWebSocket) {
            val connection = Connection(this, call)
            try {
                WebSocketManager.connections += connection
                connection.suspendProcessing()
            } finally {
                WebSocketManager.connections -= connection
                connection.logger.info("Websocket disconnected")
            }
        }
    }
}
