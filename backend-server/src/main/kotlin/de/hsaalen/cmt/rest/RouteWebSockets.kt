package de.hsaalen.cmt.rest

import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathWebSocket
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.network.dto.websocket.LiveDto
import de.hsaalen.cmt.repository.DocumentRepository
import de.hsaalen.cmt.utils.JsonHelper
import de.hsaalen.cmt.websocket.WebSocketManager
import io.ktor.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import org.koin.ktor.ext.inject


// TODO: event handlers for reference add/remove, document edit
/**
 * Handler for web-sockets that are used for live synchronisation of cloud data, e.g. file edit.
 */
fun Route.routeWebSockets() = route("/" + RestPaths.base) {
    authenticate {
        webSocket(apiPathWebSocket) {
            println("websocket connected!")
            try {
                WebSocketManager.connections += this
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val jsonText = frame.readText()
                        println("websocket: received: $jsonText")
                        val dto: DocumentChangeDto = JsonHelper.decode(jsonText)
                        val repo: DocumentRepository by inject()
                        repo.modifyDocument(dto)
                    } else {
                        println("websocket: received unknown frame: " + frame.frameType.name)
                    }
                }
            } finally {
                println("websocket disconnected")
                WebSocketManager.connections -= this
            }
        }
    }
}
