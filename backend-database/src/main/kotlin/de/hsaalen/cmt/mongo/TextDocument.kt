package de.hsaalen.cmt.mongo

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TextDocument(
    @Contextual @SerialName("_id") val uuid: String,
    var lines: Map<Int, String>
) {

    var linesAsArray: List<String>
        get() {
            val size = lines.size
            return List(size) { index ->
                lines[index]
                    ?: throw IllegalStateException("No line found for index $index, but document has $size lines")
            }
        }
        set(list) {
            lines = list.indices.zip(list).toMap()
        }

    constructor(id: String, vararg lines: String) : this(id, emptyMap()) {
        linesAsArray = lines.toList()
    }
}
