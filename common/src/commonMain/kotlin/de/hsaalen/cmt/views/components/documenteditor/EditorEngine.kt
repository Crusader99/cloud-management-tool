package de.hsaalen.cmt.views.components.documenteditor

import de.crusader.extensions.EnumDirection

/**
 * Backend system for modifying the text content with multiple cursors in live mode.
 */
class EditorEngine(
    val isMultiLineAllowed: Boolean = true,
    val defaultLine: Line,
) {
    val lines = mutableListOf(defaultLine)

    val cursor = Cursor(CursorReference(this, CursorOwner(), 0), CursorPos(0, 0))

    var text: String
        get() = lines.joinToString("\n")
        set(value) {
            lines.clear()
            for (line in value.lineSequence()) {
                lines += Line(line)
            }
        }

    fun clear() {
        lines.clear()
        lines += defaultLine // Only keep one empty line
    }

    class Line(defaultText: String) {
        val items = mutableListOf<Char>()

        val size: Int
            get() = items.size

        init {
            items.addAll(defaultText.toList())
        }

        override fun toString() = items.toCharArray().concatToString()
    }

    class CursorOwner()

    data class CursorReference(
        val engine: EditorEngine,
        val owner: CursorOwner,
        val index: Int
    )

    class Cursor(
        val ref: CursorReference,
        pos: CursorPos,
    ) {

        var pos: CursorPos = pos
            private set

        val x get() = pos.x

        val y get() = pos.y

        val isAtStartOfDocument
            get() = isOnFirstLine && isAtStartOfLine

        val isAtEndOfDocument
            get() = isOnLastLine && isAtEndOfLine

        val isAtStartOfLine
            get() = x <= 0

        val isAtEndOfLine
            get() = x >= line.size

        val isOnFirstLine
            get() = y <= 0

        val isOnLastLine
            get() = y >= ref.engine.lines.lastIndex

        val line: Line
            get() = ref.engine.lines[y]

        /**
         * Moves the cursor in text content, returns true when operation successful.
         */
        fun move(direction: EnumDirection, size: Int = 1): Boolean {
            val expectedNewPosition = pos.add(direction.x * size, direction.y * size)
            set(expectedNewPosition)
            return pos == expectedNewPosition
        }

        fun deletePreviousChar() {
            if (!isAtStartOfLine) {
                move(EnumDirection.LEFT, 1)
                line.items.removeAt(x)
            }
        }

        fun deleteFollowingChar() {
            if (!isAtEndOfLine) {
                line.items.removeAt(x)
            }
        }

        fun set(newPos: CursorPos) {
            if (pos == newPos) {
                return
            }
            val y = newPos.y.coerceIn(ref.engine.lines.indices)
            val x = newPos.x.coerceIn(0..ref.engine.lines[y].size)
            pos = CursorPos(x, y)
        }

        fun insert(text: String, moveCursor: Boolean = true) {
            line.items.addAll(x, text.toList())
            if (moveCursor) {
                move(EnumDirection.RIGHT, text.length)
            }
        }

        fun newLine() {
            ref.engine.lines.add(y, ref.engine.defaultLine)
        }
    }

    data class CursorPos(
        val x: Int,
        val y: Int
    ) {
        fun x(modify: (oldX: Int) -> Int) = copy(x = modify(x))

        fun y(modify: (oldY: Int) -> Int) = copy(y = modify(y))

        fun add(addX: Int, addY: Int) = copy(x = x + addX, y = y + addY)
    }
}