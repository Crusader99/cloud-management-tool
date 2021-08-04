package de.hsaalen.cmt.events.handlers

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import de.hsaalen.cmt.EnumPageType
import de.hsaalen.cmt.events.*
import de.hsaalen.cmt.file.readBytes
import de.hsaalen.cmt.file.readText
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.objects.ContentType
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.utils.SimpleNoteImportJson
import mu.KotlinLogging
import react.setState

/**
 * Global event handlers for the GUI application.
 */
object ReferenceEventHandlers {
    /**
     * Logging instance for this class.
     */
    private val logger = KotlinLogging.logger("ReferenceEventHandlers")

    /**
     * Initialize global reference event handlers.
     */
    fun init() {
        GlobalEventDispatcher.createBundle(this) {
            // Clientside events
            register(EventType.PRE_DOCUMENT_IMPORT, ReferenceEventHandlers::onImportDocuments)
            register(EventType.PRE_CREATE_NEW_DOCUMENT, ReferenceEventHandlers::onCreateReference)
            register(EventType.PRE_FILE_UPLOAD, ReferenceEventHandlers::onUploadFile)
            register(EventType.PRE_USER_OPEN_REFERENCE, ReferenceEventHandlers::onReferenceOpen)
        }
    }

    /**
     * Import data from simplenote json format.
     */
    private suspend fun onImportDocuments() {
        suspend fun importFile(name: String, content: String) {
            logger.info { "importing..." }
            val session = Session.instance!!
            when {
                name == "notes.json" -> SimpleNoteImportJson.import(content).forEach { session.createReference(it) }
                name.endsWith(".txt", true) -> session.createReferenceToDocument(name, content)
                else -> throw UnsupportedOperationException("File format unsupported: $name")
            }
        }

        try {
            GuiOperations.loading {
                for (file in GuiOperations.showFileSelector()) {
                    logger.info { "Importing " + file.name + "..." }
                    val text = file.readText()
                    importFile(file.name, text)
                    logger.info { file.name + " successfully imported" }
                }
            }
        } catch (ex: Exception) {
            logger.warn(ex) { "Document import failed" }
            GuiOperations.showSnackBar(ex.message ?: return, MAlertSeverity.warning)
        }
    }

    /**
     * Create a new reference object on server.
     */
    private suspend fun onCreateReference() {
        try {
            val displayName = GuiOperations.showInputDialog(
                title = "Name for new reference",
                placeholder = "Display name",
                button = "Create"
            ) ?: return
            logger.info { "Selected display name: $displayName" }
            GuiOperations.loading {
                Session.instance?.createReferenceToDocument(displayName)
            }
        } catch (ex: Exception) {
            logger.warn(ex) { "Create reference failed" }
            GuiOperations.showSnackBar(ex.message ?: return, MAlertSeverity.warning)
        }
    }

    /**
     * Called when user tries to upload a new file to server.
     */
    private suspend fun onUploadFile() {
        try {
            GuiOperations.loading {
                val files = GuiOperations.showFileSelector()
                if (files.isEmpty()) {
                    return
                }
                val session = Session.instance ?: return
                for (file in files) {
                    val createDto = ClientCreateReferenceDto(displayName = file.name, contentType = ContentType.FILE)
                    val reference = session.createReference(createDto)
                    session.upload(reference.uuid, file.readBytes())
                }
            }
        } catch (ex: Exception) {
            logger.warn(ex) { "File upload failed" }
            GuiOperations.showSnackBar(ex.message ?: return, MAlertSeverity.warning)
        }
    }

    /**
     * Called when user clicks on a reference item.
     */
    private suspend fun onReferenceOpen(event: ReferenceEvent) {
        if (event.reference.contentType == ContentType.TEXT) {
            GuiOperations.webApp.setState {
                reference = event.reference
                page = EnumPageType.EDIT_DOCUMENT
            }
        } else {
            val type = event.reference.contentType
            val message = "Type $type does not support live edit"
            GuiOperations.showSnackBar(message, MAlertSeverity.warning)
            logger.warn { message }
        }
    }

}
