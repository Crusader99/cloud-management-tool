package de.hsaalen.cmt.extensions

import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.FileList
import org.w3c.files.FileReader
import org.w3c.files.get
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Suspend function for asking the user to select files.
 */
suspend fun openFileSelector(): List<File> {
    val fileSelector = document.createElement("input") as HTMLInputElement
    fileSelector.type = "file"
    fileSelector.multiple = false

    val files: FileList = suspendCoroutine { continuation ->
        fun handle() {
            val files = fileSelector.files ?: return
            continuation.resume(files)
        }

        fileSelector.onchange = { handle() }
        fileSelector.onblur = { handle() }
        fileSelector.click()
    }

    val list = mutableListOf<File>()
    repeat(files.length) { index ->
        list += files[index] ?: return list
    }
    return list
}

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
