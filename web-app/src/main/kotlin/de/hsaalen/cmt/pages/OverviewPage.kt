package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.referenceList
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.network.session.Session
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import react.*

/**
 * The overview app component for displaying results of the search.
 */
class OverviewPage : RComponent<OverviewPage.Props, OverviewPage.State>() {

    interface Props : RProps {
        var session: Session
        var onItemOpen: (Reference) -> Unit
    }

    interface State : RState {
        var query: ClientReferenceQueryDto
        var dto: ServerReferenceListDto?
    }

    override fun State.init() {
        query = ClientReferenceQueryDto()
        dto = null
        GlobalScope.launch {
            val received = props.session.listReferences(query)
            setState {
                dto = received
            }
        }
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        referenceList(dto = state.dto, onItemOpen = props.onItemOpen)
    }

}
