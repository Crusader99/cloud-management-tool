package de.hsaalen.cmt.components.documenteditor

import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.rsocket.CursorUpdateDto
import de.hsaalen.cmt.network.dto.rsocket.LiveDto
import de.hsaalen.cmt.utils.addCursors
import de.hsaalen.cmt.utils.cursorCharacter
import kotlinx.atomicfu.AtomicInt
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.w3c.dom.HTMLTextAreaElement
import react.RReadableRef

/**
 * Engine for handling text area content.
 */
class TextareaEngine(
    private val textarea: RReadableRef<HTMLTextAreaElement>,
    private val mouseDowns: AtomicInt,
    private val channelSend: Channel<LiveDto>
) : Engine {

    /**
     * Local logging instance.
     */
    private val logger = KotlinLogging.logger("TextareaEngine")

    /**
     * Store the last position of the own cursor to detect changes.
     */
    private var lastCursorPosition: Int? = null

    /**
     * The current cursors of other text engines.
     */
    private val cursors = mutableMapOf<UUID, Int>()

    /**
     * The cursors before text update. This is required to detect the differences.
     */
    private var previousCursors = mapOf<UUID, Int>()

    /**
     * Task to send cursor position to other text engines periodically.
     */
    init {
        coroutines.launch {
            while (isActive) {
                delay(100)
                val selection = textarea.current?.selectionStart ?: 0
                try {
                    if (selection != lastCursorPosition) {
                        // Send cursor update to server when changed
                        val actualCursorPosition = selection - cursors.values.count { it < selection }
                        channelSend.send(CursorUpdateDto(cursorIndex = actualCursorPosition))
                    }
                } finally {
                    lastCursorPosition = selection
                    // Don't update selection when currently selection
                    if (mouseDowns.value <= 0) {
                        text = text // Will update the cursor positions
                    }
                }
            }
        }
    }


    /**
     * Modifies the text without resetting the cursor.
     */
    override var text: String
        get() = textarea.current?.value?.replace(cursorCharacter, "") ?: ""
        set(value) {
            // There is a mismatch between real cursor position and actual cursor position due to fake cursors
            val element = textarea.current ?: return
            val cursorStart = element.selectionStart ?: 0
            val cursorEnd = element.selectionEnd ?: 0
            val actualCursorStart = cursorStart - previousCursors.values.count { it < cursorStart }
            val actualCursorEnd = cursorEnd - previousCursors.values.count { it < cursorEnd }

            // Add virtual cursors to new text
            val cleanValue = value.replace(cursorCharacter, "")
            var textWithCursors = cleanValue
            try {
                val otherCursorIndices = cursors.values
                textWithCursors = cleanValue.addCursors(otherCursorIndices.toSet())
                if (textWithCursors.replace(cursorCharacter, "") != cleanValue) {
                    logger.warn { "Failed cursor adding: '$cleanValue' with cursors $otherCursorIndices! Result: $textWithCursors" }
                }
            } catch (ex: Exception) {
                logger.warn(ex) { "Can not update cursor position" }
            }
            textarea.current?.value = textWithCursors

            // Correct cursor position after the text was updated
            val newCursorStart = actualCursorStart + cursors.values.count { it <= cursorStart }
            val newCursorEnd = actualCursorEnd + cursors.values.count { it <= cursorEnd }
            element.setSelectionRange(newCursorStart, newCursorEnd)
            previousCursors = cursors.toMap()
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

    /**
     * Update the cursor position of another text engine.
     */
    override fun updateCursor(engineId: UUID, cursorPosition: Int?) {
        if (cursorPosition == null) {
            cursors -= engineId
        } else {
            cursors[engineId] = cursorPosition
        }
        text = text // Will update the cursor positions
    }

}
