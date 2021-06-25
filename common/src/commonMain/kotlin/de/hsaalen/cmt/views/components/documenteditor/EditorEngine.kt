package de.hsaalen.cmt.views.components.documenteditor

import de.crusader.extensions.EnumDirection

/**
 * Backend system for modifying the text content with multiple cursors in live mode.
 */
class EditorEngine(
    val isMultiLineAllowed: Boolean = true
) {
    val lines = mutableListOf<Line>()

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
        lines += newEmptyLine() // Only keep one empty line
    }

    private fun newEmptyLine() = Line(mutableListOf())

    class Line(charItems: List<Char>) {
        val items = charItems.toMutableList()

        val size: Int
            get() = items.size

        constructor(defaultText: String) : this(defaultText.toMutableList())

        override fun toString() = items.toCharArray().concatToString()

    }

    fun modifyLine(lineNumber: Int, newContent: String) {
//        val line = lines[lineNumber] // TODO: enable for other cursors
//        for(index in line.items.indices){
//            val oldChar = line.items[index]
//            val newChar = newContent.getOrNull(index)
//        }

        lines[lineNumber] = Line(newContent)
    }

    fun addLine(lineNumber: Int, lineContent: String) {
        lines.add(lineNumber, Line(lineContent))
//        if (cursor.y >= lineNumber) { // TODO: enable for other cursors
//            cursor.move(EnumDirection.DOWN, 1)
//        }
    }

    fun deleteLine(lineNumber: Int) {
        lines.removeAt(lineNumber)
//        if (cursor.y > lineNumber) { // TODO: enable for other cursors
//            cursor.move(EnumDirection.UP, 1)
//        }
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
            ref.engine.lines.add(y + 1, ref.engine.newEmptyLine())
            move(EnumDirection.DOWN)
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
