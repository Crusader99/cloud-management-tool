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

    fun clear() {
        lines.clear()
        lines += defaultLine // Only keep one empty line
    }

    class Line(defaultText: String) {
        private val items = mutableListOf<Char>()

        val size: Int
            get() = items.size

        init {
            insert(0, *defaultText.toCharArray())
        }

        fun insert(index: Int, vararg c: Char) {
            items.addAll(index, c.toList())
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

        val isAtBeginning
            get() = y <= 0 && x <= 0

        val isAtEnd
            get() = y >= ref.engine.lines.lastIndex && x >= ref.engine.lines.last().size

        val line: Line
            get() = ref.engine.lines[y]

        /**
         * Moves the cursor in text content, returns true when operation successful.
         */
        fun move(direction: EnumDirection): Boolean {
            val expectedNewPosition = pos.add(direction.x, direction.y)
            set(expectedNewPosition)
            return pos == expectedNewPosition
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
            line.insert(x, *text.toCharArray())
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