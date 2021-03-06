package de.hsaalen.cmt.utils

import de.hsaalen.cmt.network.dto.rsocket.LiveDto
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.PayloadBuilder
import io.rsocket.kotlin.payload.data
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf

/**
 * Helper class (proxy) for encoding and decoding JSON files.
 * Used to ensure loose coupling to specific framework.
 */
object SerializeHelper {

    /**
     * Configuration for the JSON generator.
     */
    val configured = Json {
        prettyPrint = true
        isLenient = true
    }

    /**
     * Encode given data transfer object to JSON.
     */
    inline fun <reified DTO> encodeJson(dto: DTO): String = configured.encodeToString(dto)

    /**
     * Decode the JSON to a data transfer object.
     */
    inline fun <reified DTO> decodeJson(json: String): DTO = configured.decodeFromString(json)

    /**
     * Encode given data transfer object to ProtoBuf.
     */
    inline fun <reified DTO> encodeProtoBuf(dto: DTO): ByteArray = ProtoBuf.encodeToByteArray(dto)

    /**
     * Decode the ProtoBuf to a data transfer object.
     */
    inline fun <reified DTO> decodeProtoBuf(data: ByteArray): DTO = ProtoBuf.decodeFromByteArray(data)

}

/**
 * Add [LiveDto] data to rSocket payload.
 */
inline fun <reified DTO : LiveDto> PayloadBuilder.protobufData(dto: DTO) {
    data(SerializeHelper.encodeProtoBuf(dto as LiveDto))
}

/**
 * Read [LiveDto] data from rSocket payload.
 */
inline fun <reified DTO : LiveDto> Payload.decodeProtobufData(): DTO {
    val dto: LiveDto = SerializeHelper.decodeProtoBuf(data.readBytes())
    return dto as DTO
}

/**
 * Convert LiveDto to payload that can be used for rSocket transmission.
 * Note: A new payload has to be built for each client.
 */
fun LiveDto.buildPayload(): Payload {
    val dto = this
    return io.rsocket.kotlin.payload.buildPayload {
        protobufData(dto)
    }
}
