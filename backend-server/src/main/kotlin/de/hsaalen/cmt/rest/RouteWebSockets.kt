package de.hsaalen.cmt.rest

import de.hsaalen.cmt.mongo.MongoDB
import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathWebSocket
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.network.dto.websocket.LiveDto
import de.hsaalen.cmt.repositories.ReferencesRepositoryImpl
import de.hsaalen.cmt.utils.JsonHelper
import io.ktor.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

/**
 * List of all current web-socket connections.
 */
private val connections = mutableListOf<WebSocketSession>()

/**
 * Handler for web-sockets that are used for live synchronisation of cloud data, e.g. file edit.
 */
fun Route.routeWebSockets() = route("/" + RestPaths.base) {
    ReferencesRepositoryImpl.onUpdateSubscriber = ::broadcast
    authenticate {
        webSocket(apiPathWebSocket) {
            println("websocket connected!")
            try {
                connections += this
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val jsonText = frame.readText()
                        println("websocket: received: $jsonText")
                        val dto: DocumentChangeDto = JsonHelper.decode(jsonText)
                        MongoDB.updateDocument(dto)
                        broadcastToOthers(dto)
                    } else {
                        println("websocket: received unknown frame: " + frame.frameType.name)
                    }
                }
            } finally {
                println("websocket disconnected")
                connections -= this
            }
        }
    }
}

/**
 * Broadcast a DTO to all web-socket clients except to the own client.
 */
private suspend fun WebSocketSession.broadcastToOthers(dto: LiveDto) {
    val jsonText = JsonHelper.encode(dto)
    for (others in connections.filter { it != this }) {
        println("send: $jsonText")
        others.outgoing.send(Frame.Text(jsonText))
    }
}

/**
 * Broadcast a DTO to all web-socket clients.
 */
private suspend fun broadcast(dto: LiveDto) {
    val jsonText = JsonHelper.encode(dto)
    for (others in connections) {
        println("send: $jsonText")
        others.outgoing.send(Frame.Text(jsonText))
    }
}

// TODO: remove (this was experimental code with the RSocket library)
// See https://github.com/rsocket/rsocket-kotlin
//
//fun Route.handleRSocket() = rSocket("rsocket") {
//    this.requester.fireAndForget()
//    RSocketRequestHandler {
//        fireAndForget { request: Payload ->
//            println("RSocket: fireAndForget")
//            val json = request.data.readText()
////            val obj: TestDto = ProtoBuf.decodeFromString(json)
//            println("RSocket: Got a json obj with: " + obj.text)
//        }
//    }
//}
