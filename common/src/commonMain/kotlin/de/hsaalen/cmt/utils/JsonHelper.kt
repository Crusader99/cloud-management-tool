package de.hsaalen.cmt.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Helper class (proxy) for encoding and decoding JSON files.
 * Used to ensure loose coupling to specific framework.
 */
object JsonHelper {

    /**
     * Configuration for the JSON generator.
     */
    val configured = Json {
        prettyPrint = true
        isLenient = true
    }

    /**
     * Encode given data transfer object to json.
     */
    inline fun <reified DTO> encode(dto: DTO): String = configured.encodeToString(dto)

    /**
     * Decode the json to a data transfer object.
     */
    inline fun <reified DTO> decode(json: String): DTO = configured.decodeFromString(json)
}
