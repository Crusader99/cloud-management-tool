package de.hsaalen.cmt.components.documenteditor

/**
 * Engine for handling text area content.
 */
interface Engine {

    var text : String

    fun modifyLine(lineNumber: Int, newContent: String)

    fun addLine(lineNumber: Int, lineContent: String)

    fun deleteLine(lineNumber: Int)

}
