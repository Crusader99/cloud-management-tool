package de.hsaalen.cmt.utils

/**
 * A unicode character for a fake cursor.
 */
const val cursorCharacter = "\u2502"

/**
 * Add fake cursors to a provided string using the [cursorCharacter] as cursor.
 */
fun String.addCursors(cursors: Set<Int>): String {
    val txt = replace(cursorCharacter, "")
    val textParts = mutableListOf<String>()
    val endIndex = if (cursors.isEmpty()) {
        0 // No cursors so nothing to do
    } else {
        val validCursors = cursors.map { it.coerceIn(0, txt.length) }
        textParts += txt.substring(0, validCursors.first())
        validCursors.reduce { acc, i ->
            textParts += txt.substring(acc, i)
            i
        }
    }
    textParts += txt.substring(endIndex)
    return textParts.joinToString(cursorCharacter)
}
