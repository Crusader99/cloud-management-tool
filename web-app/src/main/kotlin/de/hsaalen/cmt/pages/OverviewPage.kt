package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.ViewReferenceList
import de.hsaalen.cmt.network.client.Session
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import react.*

/**
 * The overview app component for displaying results of the search.
 */
class OverviewPage : RComponent<OverviewPage.Props, OverviewPage.State>() {

    interface Props : RProps {
        var session: Session
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
        child(ViewReferenceList::class) {
            attrs {
                dto = state.dto
            }
        }
    }

}
