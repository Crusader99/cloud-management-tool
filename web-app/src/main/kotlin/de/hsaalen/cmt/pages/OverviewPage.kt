package de.hsaalen.cmt.pages

import com.ccfraser.muirwik.components.mTypography
import de.hsaalen.cmt.components.referenceList
import de.hsaalen.cmt.events.*
import de.hsaalen.cmt.extensions.launch
import de.hsaalen.cmt.file.openFileSaver
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.rsocket.LabelUpdateDto
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateAddDto
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateRemoveDto
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateRenameDto
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.network.session.Session
import kotlinx.css.TextAlign
import kotlinx.css.textAlign
import mu.KotlinLogging
import react.*
import styled.css

/**
 * React properties of the [OverviewPage] component.
 */
external interface OverviewPageProps : RProps {
    var session: Session
}

/**
 * React state of the [OverviewPage] component.
 */
external interface OverviewPageState : RState {
    var query: ClientReferenceQueryDto
    var dto: ServerReferenceListDto
    var isLoading: Boolean
}

/**
 * The overview app component for displaying results of the search.
 */
@JsExport
class OverviewPage : RComponent<OverviewPageProps, OverviewPageState>() {

    /**
     * Logging instance for this class.
     */
    private val logger = KotlinLogging.logger("OverviewPage")

    /**
     * Register events for this component.
     */
    private val events = GlobalEventDispatcher.createBundle(this) {
        // Serverside events
        register(::onServerAddedReference)
        register(::onServerDeleteReference)
        register(::onServerRenamedReference)
        register(::onServerLabelUpdate)

        // Clientside events
        register(EventType.PRE_USER_DELETE_REFERENCE, ::onClientReferenceDelete)
        register(EventType.PRE_USER_DOWNLOAD_REFERENCE, ::onClientReferenceDownload)
        register(EventType.PRE_USER_RENAME_REFERENCE, ::onClientReferenceRename)
        register(EventType.PRE_USER_ADD_LABEL, ::onClientLabelAdd)
        register(EventType.PRE_USER_REMOVE_LABEL, ::onClientLabelRemove)

        // Initialize reference list
        launch(::updateReferences)
    }

    /**
     * Initialize state of the [OverviewPage].
     */
    override fun OverviewPageState.init() {
        isLoading = true
        query = ClientReferenceQueryDto()
        dto = ServerReferenceListDto(emptyList())
    }

    /**
     * Remove registered event handlers.
     */
    override fun componentWillUnmount() {
        events.unregisterAll()
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        referenceList(dto = state.dto)
        if (state.dto.references.isEmpty() && !state.isLoading) {
            mTypography("No references found. Open left side menu to create new references.") {
                css {
                    textAlign = TextAlign.center
                }
            }
        }
    }

    /**
     * Request a references update from server.
     */
    private suspend fun updateReferences() {
        GuiOperations.loading {
            try {
                val received = props.session.listReferences(state.query)
                setState {
                    dto = received
                }
            } finally {
                setState {
                    isLoading = false
                }
            }
        }
    }

    /**
     * Allow downloading selected reference to local computer.
     */
    private suspend fun onClientReferenceDownload(event: ReferenceEvent) {
        val content = Session.instance?.downloadContent(event.reference.uuid)
        if (content == null) {
            logger.error { "Content not available. Check server connection." }
        } else {
            openFileSaver(event.reference.displayName + ".txt", content)
        }
    }

    /**
     * Request server to delete a reference.
     */
    private suspend fun onClientReferenceDelete(event: ReferenceEvent) {
        Session.instance?.deleteReference(event.reference)
    }

    /**
     * Request server to rename a reference.
     */
    private suspend fun onClientReferenceRename(event: ReferenceEvent) {
        val oldTitle = event.reference.displayName
        val message = "New title for reference '$oldTitle':"
        val newTitle = GuiOperations.showInputDialog("Rename", message, defaultValue = oldTitle)
        if (oldTitle != newTitle) {
            Session.instance?.rename(event.reference.uuid, newTitle ?: return)
        }
    }

    /**
     * Called when user adds a label to a reference.
     */
    private suspend fun onClientLabelAdd(event: ReferenceEvent) {
        val labelName = GuiOperations.showInputDialog(
            title = "Add label to \"" + event.reference.displayName + "\":",
            placeholder = "label name",
            button = "Add"
        ) ?: return
        logger.info { "Add label name: $labelName" }
        Session.instance?.addLabel(event.reference, labelName)
    }

    /**
     * Called when user removes a label from a reference.
     */
    private suspend fun onClientLabelRemove(event: LabelEditEvent) {
        Session.instance?.removeLabel(event.reference, event.labelName)
    }

    /**
     * Event called by server after new reference added.
     */
    private fun onServerAddedReference(ref: ReferenceUpdateAddDto) {
        logger.info { "Received ReferenceUpdateAddDto" }
        setState {
            val new = listOf(ref.reference)
            val old = dto.references
            dto = ServerReferenceListDto(new + old)
        }
    }

    /**
     * Event called by server after a reference got deleted.
     */
    private fun onServerDeleteReference(ref: ReferenceUpdateRemoveDto) {
        logger.info { "Received ReferenceUpdateRemoveDto" }
        setState {
            val old = dto.references
            dto = ServerReferenceListDto(old.filter { it.uuid != ref.uuid })
        }
    }

    /**
     * Event called by server after a reference got renamed.
     */
    private fun onServerRenamedReference(event: ReferenceUpdateRenameDto) {
        logger.info { "Received ReferenceUpdateRenameDto" }
        setState {
            val referenceList = dto.references
            val reference = referenceList.find { it.uuid == event.uuid } ?: return@setState
            reference.displayName = event.newName
            dto = ServerReferenceListDto(referenceList)
        }
    }

    /**
     * Event called by server after a label was added / removed.
     */
    private fun onServerLabelUpdate(event: LabelUpdateDto) {
        logger.info { "Received LabelUpdateDto" }
        setState {
            val ref = dto.references.find { it.uuid == event.reference } ?: return@setState
            when (event.mode) {
                LabelChangeMode.ADD -> ref.labels += event.labelName
                LabelChangeMode.REMOVE -> ref.labels -= event.labelName
            }
        }
    }
}
