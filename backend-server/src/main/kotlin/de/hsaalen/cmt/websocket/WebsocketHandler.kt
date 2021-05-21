package de.hsaalen.cmt.websocket

import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*

/**
 * Handler for web-sockets that are used for live synchronisation of cloud data, e.g. file edit.
 */
fun Route.handleWebSocket() = webSocket("/websocket") {
    println("websocket connected!")
    try {
        outgoing.send(Frame.Text("test"))
        for (frame in incoming) {
            if (frame is Frame.Text) {
                val text = frame.readText()
                println("websocket: received: $text")
                outgoing.send(Frame.Text("received: $text"))
            } else {
                println("websocket: received unknown frame: " + frame.frameType.name)
            }
        }
    } finally {
        println("websocket disconnected")
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
