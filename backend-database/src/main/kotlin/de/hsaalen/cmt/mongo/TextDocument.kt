package de.hsaalen.cmt.mongo

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TextDocument(
    @Contextual @SerialName("_id") val uuid: String,
    var lines: List<String>
) {

    constructor(id: String, vararg lines: String) : this(id, lines.toList())

}
