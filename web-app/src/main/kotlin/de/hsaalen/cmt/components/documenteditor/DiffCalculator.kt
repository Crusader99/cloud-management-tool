package de.hsaalen.cmt.components.documenteditor

import de.hsaalen.cmt.network.dto.objects.LineChangeMode
import mu.KotlinLogging
import kotlin.math.abs

/**
 * Algorithm to detect changed lines in changed text documents. This is used for text documents to improve performance
 * when sending changes to server/mongo-database.
 *
 * Note: This a completely self developed algorithm that I already used for other projects.
 *
 * @author Simon Forschner
 */
class DiffCalculator(
    /**
     * Event to be called when line is changed.
     */
    private val onChangeLine: (lineNumber: Int, lineContent: String, changeMode: LineChangeMode) -> Unit
) {
    /**
     * Local logging instance.
     */
    private val logger = KotlinLogging.logger { }

    /**
     * The last document lines before call of [findChangedLines] function.
     */
    private var previousLines = emptyList<String>()

    /**
     * Algorithm to detect changes, inserts or deletion in a text document. This is used for text documents to improve
     * performance when sending changes to server/mongo-database.
     */
    fun findChangedLines(newText: String) {
        // Split the text document in lines
        val currentLines = newText.split("\n")
        try {
            findChangedLines(currentLines)
        } finally {
            // Update lines for next changes
            previousLines = currentLines
        }
    }

    /**
     * Function to set current text without detecting changes.
     */
    fun setText(overwriteText: String) {
        previousLines = overwriteText.lines()
    }

    /**
     * Algorithm to detect changes, inserts or deletion in a text document. This is used for text documents to improve
     * performance when sending changes to server/mongo-database.
     */
    private fun findChangedLines(currentLines: List<String>) {
        val changedRange = findDiffRange(currentLines)
        var lineDiff = currentLines.size - previousLines.size
        logger.info {
            val oldLines = previousLines.size
            val newLines = currentLines.size
            "findChangedLines: changedRange=$changedRange lineDiff=$lineDiff Lines (old=$oldLines new=$newLines)"
        }

        // Count down to prevent changed indices after line remove
        for (c in 0 until changedRange.last - changedRange.first) {
            val i = if (currentLines.size < previousLines.size) {
                // Count down to prevent changed indices after line remove
                changedRange.last - c - 1
            } else {
                changedRange.first + c
            }

            if (lineDiff > 0) { // New lines created in edited note version
                logger.info { "insert line at " + i + " (after " + (i - 1) + "): " + currentLines[i] }
                lineDiff--
                onChangeLine(i, currentLines[i], LineChangeMode.ADD)
            } else if (lineDiff < 0) {
                // Lines removed in edited note version
                logger.info { "remove line at $i" }
                lineDiff++
                onChangeLine(i, "", LineChangeMode.DELETE)
            } else if (currentLines.size != previousLines.size || currentLines[i] !== previousLines[i]) {
                // Line only changed, not inserted or removed
                logger.info { "updated line at " + i + ": " + currentLines[i] }
                onChangeLine(i, currentLines[i], LineChangeMode.MODIFY)
            }
        }
    }

    /**
     * Algorithm used to calculate start and end line index of changes.
     */
    private fun findDiffRange(currentLines: List<String>): IntRange {
        val minLines = minOf(previousLines.size, currentLines.size)
        val maxLines = maxOf(previousLines.size, currentLines.size)
        val diffLines = abs(previousLines.size - currentLines.size)
        // Note: minLines + diffLines = maxLines

        var equalBeginning: Int? = null // null means more than max possible
        var equalEnding: Int? = null
        for (i in 0 until minLines) {
            if (equalBeginning == null && previousLines[i] !== currentLines[i]) {
                equalBeginning = i
            }
            val previousLastChangedLine = previousLines[previousLines.lastIndex - i]
            val currentLastChangedLine = currentLines[currentLines.lastIndex - i]
            if (equalEnding == null && previousLastChangedLine !== currentLastChangedLine) {
                equalEnding = i
            }
        }

        return when {
            equalBeginning == null && equalEnding == null && diffLines == 0 -> 0..0 // Content equals
            equalBeginning == null -> minLines..maxLines // Lines added or removed at end of smaller document
            equalEnding == null -> equalBeginning..equalBeginning + diffLines // Add/remove at start of smaller doc
            else -> {
                // Lines added or removed at specific position in document
                val endIndex = maxOf(maxLines - equalEnding, equalBeginning + diffLines)
                return equalBeginning..endIndex
            }
        }
    }
}
