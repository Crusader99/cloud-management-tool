package de.hsaalen.cmt.websocket

import de.hsaalen.cmt.network.dto.websocket.LiveDto
import de.hsaalen.cmt.utils.JsonHelper
import io.ktor.http.cio.websocket.*

/**
 * Singleton object for holding active web socket connections.
 */
object WebSocketManager {

    /**
     * List of all currently active web-socket connections.
     */
    val connections = mutableListOf<Connection>()

    /**
     * Broadcast a DTO to all web-socket clients except to the (own) client.
     */
    suspend fun broadcastExcept(excludeSocketId: String, dto: LiveDto) {
        val jsonText = JsonHelper.encode(dto)
        for (others in connections.filter { it.socketId != excludeSocketId }) {
            println("send: $jsonText")
            others.outgoing.send(Frame.Text(jsonText))
        }
    }

    /**
     * Broadcast a DTO to all web-socket clients.
     */
    suspend fun broadcast(dto: LiveDto) {
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

}
