package de.hsaalen.cmt.file

import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.events.register
import kotlinx.browser.document
import mu.KotlinLogging
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.File
import org.w3c.files.FileList
import org.w3c.files.FileReader
import org.w3c.files.get
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Suspend function to read text from file.
 */
suspend fun File.readText(): String {
    val reader = FileReader()
    val result: dynamic = suspendCoroutine { continuation ->
        reader.onload = {
            continuation.resume(reader.result)
        }
        reader.readAsText(this)
    }
    return result.toString()
}

/**
 * Provides functionality for opening a file input dialog.
 */
object FileSelector {

    /**
     * Logging instance for this class.
     */
    private val logger = KotlinLogging.logger {}

    /**
     * Suspend function for asking the user to select files.
     */
    suspend fun openDialog(): List<File> {
        // Create file dialog element
        val fileSelector = document.createElement("input") as HTMLInputElement
        fileSelector.type = "file"
        fileSelector.multiple = false

        val files: FileList = suspendCoroutine { continuation ->
            val events = GlobalEventDispatcher.createBundle(this)

            // Called when file selected or cancelled
            fun onFinished(e: Event) {
                logger.info { "On finished file selection: $e" }
                events.unregisterAll()
                val files = fileSelector.files ?: return
                continuation.resume(files)
            }

            // Register events
            events.register(fileSelector, "change", ::onFinished)
            events.register(fileSelector, "blur", ::onFinished)
            events.register(document, "focus", ::onFinished)
            events.register(document, "focusin", ::onFinished)

            // Open dialog
            fileSelector.click()
        }

        // Convert result to list of files
        val list = mutableListOf<File>()
        repeat(files.length) { index ->
            list += files[index] ?: return list
        }
        return list
    }

}
