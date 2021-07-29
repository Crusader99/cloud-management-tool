package de.hsaalen.cmt.components.documenteditor

import org.w3c.dom.HTMLTextAreaElement
import react.RReadableRef

/**
 * Engine for handling text area content.
 */
class TextareaEngine(private val textarea: RReadableRef<HTMLTextAreaElement>) : Engine {

    /**
     * Modifies the text without resetting the cursor.
     */
    override var text: String
        get() = textarea.current?.value ?: ""
        set(value) {
            val element = textarea.current ?: return
            val cursorStart = element.selectionStart ?: 0
            val cursorEnd = element.selectionEnd ?: 0
            element.value = value
            element.setSelectionRange(cursorStart, cursorEnd);
        }

    /**
     * Modify text by lines.
     */
    private var lines: List<String>
        get() = text.lines()
        set(lines) {
            text = lines.joinToString("\n")
        }

    /**
     * Can be called to modify a line at a specific position.
     */
    override fun modifyLine(lineNumber: Int, newContent: String) {
        val lines = this.lines.toMutableList()
        try {
            lines[lineNumber] = newContent
            this.lines = lines
        } catch (ex: Exception) {
            throw IllegalArgumentException("Unable to modify document line $lineNumber of " + lines.size)
        }
    }

    /**
     * Insert a new line in the given line number.
     */
    override fun addLine(lineNumber: Int, lineContent: String) {
        val lines = this.lines.toMutableList()
        try {
            lines.add(lineNumber, lineContent)
            this.lines = lines
        } catch (ex: Exception) {
            throw IllegalArgumentException("Unable to insert line at $lineNumber of " + lines.size)
        }
    }

    /**
     * Remove the line at the given line number.
     */
    override fun deleteLine(lineNumber: Int) {
        val lines = this.lines.toMutableList()
        try {
            lines.removeAt(lineNumber)
            this.lines = lines
        } catch (ex: Exception) {
            throw IllegalArgumentException("Unable to delete line at $lineNumber of " + lines.size)
        }
    }
}
