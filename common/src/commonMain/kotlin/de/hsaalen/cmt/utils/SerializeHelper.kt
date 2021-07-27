package de.hsaalen.cmt.utils

import de.hsaalen.cmt.network.dto.websocket.LiveDto
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

}

/**
 * Add [LiveDto] data to rSocket payload.
 */
inline fun <reified DTO : LiveDto> PayloadBuilder.protobufData(dto: DTO) {
    data(ProtoBuf.encodeToByteArray(dto))
}

/**
 * Read [LiveDto] data from rSocket payload.
 */
inline fun <reified DTO : LiveDto> Payload.decodeProtobufData(): DTO {
    return ProtoBuf.decodeFromByteArray(data.readBytes())
}
