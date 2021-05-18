package de.hsaalen.cmt.websocket

import de.hsaalen.cmt.network.dto.client.ClientDto
import de.hsaalen.cmt.network.dto.server.ServerDto
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.cast

// Note: This is currently under development! TODO: complete implementation
class LiveSocketImplWebSocket : LiveSocket {
    private lateinit var session: WebSocketServerSession

    private val jsonFormatter = Json {
        prettyPrint = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    private val packetHandlers = mutableListOf<PacketHandler<ClientDto>>()

    init {
        val x = object : PacketHandler<ClientDto>(ClientDto::class) {
            override fun process(socket: LiveSocket, dto: ClientDto) {

            }
        }
        registerHandler(object : PacketHandler<ClientDto>(ClientDto::class) {
            override fun process(socket: LiveSocket, dto: ClientDto) {

            }
        })
    }

    override suspend fun send(dto: ServerDto) {
        val json = jsonFormatter.encodeToString(dto)
        session.send(Frame.Text(json))
    }

    fun registerHandler(handler: PacketHandler<in ClientDto>) {
        packetHandlers += handler
//        if (dto in packetHandlers) {
//            val dtoName = dto.simpleName
//            throw IllegalStateException("Packet handler for $dtoName already registered")
//        }
//        packetHandlers[dto] = handler
    }

    suspend fun bind(session: WebSocketServerSession) {
        this.session = session
        for (frame in session.incoming) {
            if (frame is Frame.Text) {
                val json = frame.readText()
                println("websocket: received: $json")
                val dto: ClientDto = jsonFormatter.decodeFromString(json)
                for (h in packetHandlers) {
                    if (h.dtoType.isInstance(dto)) {
                        h.process(this, h.dtoType.cast(dto))
                        return
                    }
                }
                throw IllegalStateException("No handler for $dto found!")
            } else {
                println("websocket: received unknown frame: " + frame.frameType.name)
            }
        }
    }

}