package de.hsaalen.cmt.network.dto.websocket

import de.hsaalen.cmt.network.dto.objects.LineChangeMode
import kotlinx.serialization.Serializable

/**
 * Data transfer object for editing a text document.
 */
@Serializable
data class DocumentChangeDto(
    val uuid: String,
    val lineNumber: Int,
    val lineContentEncrypted: String,
    val lineChangeMode: LineChangeMode,
) : LiveDto()
