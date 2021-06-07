package de.hsaalen.cmt.extensions

import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLInputElement
import org.w3c.files.File
import org.w3c.files.FileList
import org.w3c.files.FileReader
import org.w3c.files.get

/**
 * Suspend function for asking the user to select files.
 */
suspend fun openFileSelector(): List<File> {
    val fileSelector = document.createElement("input") as HTMLInputElement
    fileSelector.type = "file"
    fileSelector.multiple = false

    val channel = Channel<FileList>()
    fun handle() {
        val files = fileSelector.files ?: return
        GlobalScope.launch {
            channel.send(files)
        }
    }

    fileSelector.onchange = { handle() }
    fileSelector.onblur = { handle() }
    fileSelector.click()
    val files = channel.receive()
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
    val channel = Channel<dynamic>()
    reader.onload = {
        GlobalScope.launch {
            channel.send(reader.result)
        }
    }
    reader.readAsText(this)
    return channel.receive().toString()
}
