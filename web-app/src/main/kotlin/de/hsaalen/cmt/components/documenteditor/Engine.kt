package de.hsaalen.cmt.components.documenteditor

import de.hsaalen.cmt.network.dto.objects.UUID

/**
 * Engine for handling text area content.
 */
interface Engine {

    /**
     * Modifies the text without resetting the cursor.
     */
    var text: String

    /**
     * Can be called to modify a line at a specific position.
     */
    fun modifyLine(lineNumber: Int, newContent: String)

    /**
     * Insert a new line in the given line number.
     */
    fun addLine(lineNumber: Int, lineContent: String)

    /**
     * Remove the line at the given line number.
     */
    fun deleteLine(lineNumber: Int)

    /**
     * Called by server to update cursor position of another editor instance.
     */
    fun updateCursor(engineId: UUID, cursorPosition: Int?)

}
