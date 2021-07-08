package de.hsaalen.cmt.file

import de.crusader.extensions.toUriStr
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement

/**
 * Open native file download dialog in browser to provide a file for saving in local computer.
 */
fun openFileSaver(filename: String, content: String) {
    // Filter unknown characters from file
    var actualFileName = filename.filter { it.isLetterOrDigit() || it == '_' || it == '.' || it == ' ' }.trim()
    if (actualFileName.length > 30) {
        val fileEnding = actualFileName.substringAfterLast(".", missingDelimiterValue = "")
        actualFileName = actualFileName.substring(0, 20).substringBeforeLast(".").trim()
        if (fileEnding.length in 1..4) {
            actualFileName += ".$fileEnding"
        }
    }

    // Open file saver
    val element = document.createElement("a") as HTMLAnchorElement
    element.href = "data:text/plain;charset=utf-8," + content.toUriStr()
    element.download = actualFileName
    element.click()
}
