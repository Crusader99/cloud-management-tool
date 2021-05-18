package de.hsaalen.cmt.websocket

import de.hsaalen.cmt.network.dto.client.ClientDto
import kotlin.reflect.KClass

// Note: This is currently under development! TODO: complete implementation
abstract class PacketHandler<DTO : ClientDto>(val dtoType: KClass<DTO>) {

    abstract fun process(socket: LiveSocket, dto: DTO)

}
