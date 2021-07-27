package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.dialogs.InputDialogComponent
import de.hsaalen.cmt.components.dialogs.renderInputDialog
import de.hsaalen.cmt.components.dialogs.show
import de.hsaalen.cmt.components.referenceList
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.extensions.ReferenceListener
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.file.openFileSaver
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.network.dto.websocket.LabelUpdateDto
import de.hsaalen.cmt.network.dto.websocket.ReferenceUpdateAddDto
import de.hsaalen.cmt.network.dto.websocket.ReferenceUpdateRemoveDto
import de.hsaalen.cmt.network.session.Session
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.w3c.dom.events.Event
import react.*

/**
 * React properties of the [OverviewPage] component.
 */
external interface OverviewPageProps : RProps {
    var session: Session
    var onItemOpen: ReferenceListener
}

/**
 * React state of the [OverviewPage] component.
 */
external interface OverviewPageState : RState {
    var query: ClientReferenceQueryDto
    var dto: ServerReferenceListDto?
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
        register(::onAddedReference)
        register(::onRemovedReference)
        register(::onLabelRemoved)
    }

    /**
     * Reference to create dialog for requesting user to type a specific reference name.
     */
    private val refCreateLabelDialog = createRef<InputDialogComponent>()

    /**
     * Initialize state of the [OverviewPage].
     */
    override fun OverviewPageState.init() {
        query = ClientReferenceQueryDto()
        dto = null
        coroutines.launch {
            updateReferences()
        }
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
        renderInputDialog(refCreateLabelDialog)
        referenceList(
            dto = state.dto,
            onItemOpen = props.onItemOpen,
            onItemDelete = ::onItemDelete,
            onItemDownload = ::onItemDownload,
            onLabelAdd = ::onLabelAdd,
            onLabelRemove = ::onLabelRemove
        )
    }

    /**
     * Request a references update from server.
     */
    private suspend fun updateReferences() {
        val received = props.session.listReferences(state.query)
        setState {
            dto = received
        }
    }

    /**
     * Allow downloading selected reference to local computer.
     */
    private fun onItemDownload(event: Event, ref: Reference) {
        event.stopPropagation() // Prevent parent to receive onClick event, which would open the reference
        coroutines.launch {
            val content = Session.instance?.downloadContent(ref.uuid)
            if (content == null) {
                logger.error { "Content not available. Check server connection." }
            } else {
                openFileSaver(ref.displayName + ".txt", content)
            }
        }
    }

    /**
     * Request server to delete a reference.
     */
    private fun onItemDelete(event: Event, ref: Reference) {
        event.stopPropagation() // Prevent parent to receive onClick event, which would open the reference
        coroutines.launch {
            Session.instance?.deleteReference(ref)
        }
    }

    /**
     * Called when user adds a label to a reference.
     */
    private fun onLabelAdd(event: Event, ref: Reference) {
        event.stopPropagation() // Prevent parent to receive onClick event, which would open the reference
        coroutines.launch {
            val labelName = refCreateLabelDialog.current?.show(
                title = "Add label to \"" + ref.displayName + "\":",
                placeholder = "label name",
                button = "Add"
            ) ?: return@launch
            logger.info { "Add label name: $labelName" }
            Session.instance?.addLabel(ref, labelName)
        }
    }

    /**
     * Called when user removes a label from a reference.
     */
    private fun onLabelRemove(event: Event, ref: Reference, label: String) {
        event.stopPropagation() // Prevent parent to receive onClick event, which would open the reference
        coroutines.launch {
            Session.instance?.removeLabel(ref, label)
        }
    }

    /**
     * Event called by server after new reference added.
     */
    private fun onAddedReference(ref: ReferenceUpdateAddDto) {
        logger.info { "Received ReferenceUpdateAddDto" }
        setState {
            val new = listOf(ref.reference)
            val old = dto?.references ?: emptyList()
            dto = ServerReferenceListDto(new + old)
        }
    }

    /**
     * Event called by server after a reference got deleted.
     */
    private fun onRemovedReference(ref: ReferenceUpdateRemoveDto) {
        logger.info { "Received ReferenceUpdateRemoveDto" }
        setState {
            val old = dto?.references ?: emptyList()
            dto = ServerReferenceListDto(old.filter { it.uuid != ref.uuid })
        }
    }

    /**
     * Event called by server after a label was added / removed.
     */
    private fun onLabelRemoved(event: LabelUpdateDto) {
        logger.info { "Received LabelUpdateDto" }
        setState {
            val ref = dto?.references?.find { it.uuid == event.reference } ?: return@setState
            when (event.mode) {
                LabelChangeMode.ADD -> ref.labels += event.labelName
                LabelChangeMode.REMOVE -> ref.labels -= event.labelName
            }
        }
    }
}
