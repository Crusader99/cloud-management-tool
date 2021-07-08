package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.referenceList
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.file.openFileSaver
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.network.dto.websocket.ReferenceUpdateAddDto
import de.hsaalen.cmt.network.dto.websocket.ReferenceUpdateRemoveDto
import de.hsaalen.cmt.network.session.Session
import kotlinx.browser.window
import kotlinx.coroutines.launch
import react.*

/**
 * React properties of the [OverviewPage] component.
 */
external interface OverviewPageProps : RProps {
    var session: Session
    var onItemOpen: (Reference) -> Unit
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
     * Initialize state of the [OverviewPage].
     */
    override fun OverviewPageState.init() {
        query = ClientReferenceQueryDto()
        dto = null
        coroutines.launch {
            updateReferences()
        }
        GlobalEventDispatcher.register(::onAddedReference)
        GlobalEventDispatcher.register(::onRemovedReference)
    }

    /**
     * Remove registered event handlers.
     */
    override fun componentWillUnmount() {
        GlobalEventDispatcher.unregisterAll(this::class)
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        referenceList(
            dto = state.dto,
            onItemOpen = props.onItemOpen,
            onItemDelete = ::onItemDelete,
            onItemDownload = ::onItemDownload
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
    private fun onItemDownload(ref: Reference) {
        coroutines.launch {
            val content = Session.instance?.download(ref.uuid)
            if (content == null) {
                window.alert("Content not available. Check server connection.")
            } else {
                openFileSaver(ref.displayName + ".txt", content)
            }
        }
    }

    /**
     * Request server to delete a reference.
     */
    private fun onItemDelete(ref: Reference) {
        coroutines.launch {
            Session.instance?.deleteReference(ref)
        }
    }

    /**
     * Event called by server after new reference added.
     */
    private fun onAddedReference(ref: ReferenceUpdateAddDto) {
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
        setState {
            val old = dto?.references ?: emptyList()
            dto = ServerReferenceListDto(old.filter { it.uuid != ref.uuid })
        }
    }

}
