package de.hsaalen.cmt.components.documenteditor

/**
 * Engine for handling text area content.
 */
class AceEngine(val editor: () -> dynamic) : Engine {

    private val session
        get() = editor().getSession()


    private val document
        get() = session.getDocument()

    /**
     * Modifies the text without resetting the cursor.
     */
    override var text: String
        get() = document.getValue() as String
        set(value) {
            document.setValue(value)
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
//    /**
//     * Can be called to modify a line at a specific position.
//     */
//    override fun modifyLine(lineNumber: Int, newContent: String) {
//        try {
//            deleteLine(lineNumber)
//            addLine(lineNumber, newContent)
//        } catch (ex: Exception) {
//            throw IllegalArgumentException("Unable to modify document line $lineNumber of " + lines.size)
//        }
//    }
//
//    /**
//     * Insert a new line in the given line number.
//     */
//    override fun addLine(lineNumber: Int, lineContent: String) {
//        try {
//            // Note: insertLines is deprecated, but not still in documentation
//            // (See https://ace.c9.io/#nav=api&api=editor)
//            document.insertFullLines(lineNumber, arrayOf(lineContent))
//        } catch (ex: Exception) {
//            throw IllegalArgumentException("Unable to insert line at $lineNumber of " + lines.size)
//        }
//    }
//
//    /**
//     * Remove the line at the given line number.
//     */
//    override fun deleteLine(lineNumber: Int) {
//        try {
//            document.removeNewLine(lineNumber)
//        } catch (ex: Exception) {
//            throw IllegalArgumentException("Unable to delete line at $lineNumber of " + lines.size)
//        }
//    }

}
