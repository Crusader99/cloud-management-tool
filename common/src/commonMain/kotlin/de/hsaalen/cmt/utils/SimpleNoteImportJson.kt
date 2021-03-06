package de.hsaalen.cmt.utils

import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.objects.ContentType
import kotlinx.serialization.Serializable

/**
 * Represents the data structure of the simplenote format.
 */
@Serializable
data class SimpleNoteImportJson(
    val activeNotes: List<SimpleNote>,
    val trashedNotes: List<SimpleNote>,
) {
    companion object {
        /**
         * Import data from simplenote json format.
         */
        fun import(json: String): List<ClientCreateReferenceDto> {
            val data: SimpleNoteImportJson = SerializeHelper.decodeJson(json)
            return data.activeNotes.map { note ->
                ClientCreateReferenceDto(note.title, ContentType.TEXT, note.content.lines(), labels = note.tags)
            }
        }
    }
}

/**
 * A single element of the simplenote data.
 */
@Serializable
data class SimpleNote(
    val id: String? = null,
    val content: String,
    val creationDate: String,
    val lastModified: String,
    val markdown: Boolean = false,
    val tags: Set<String> = emptySet(),
) {

    /**
     * Generate title by the first line of document
     */
    val title: String
        get() = content.lineSequence().first()
}
