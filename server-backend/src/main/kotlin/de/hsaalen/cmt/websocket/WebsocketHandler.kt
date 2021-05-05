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

