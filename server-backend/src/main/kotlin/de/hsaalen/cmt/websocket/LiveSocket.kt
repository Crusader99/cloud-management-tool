package de.hsaalen.cmt.websocket

import de.hsaalen.cmt.network.dto.server.ServerDto

// Note: This is currently under development! TODO: complete implementation
interface LiveSocket {

    suspend fun send(dto: ServerDto)

//    fun <DTO : ClientDto> registerHandler(dto: KClass<DTO>, handler: PacketHandler<DTO>)

}
