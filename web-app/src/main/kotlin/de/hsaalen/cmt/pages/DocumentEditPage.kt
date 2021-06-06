package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.documentEditor
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.session.Session
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import react.*

/**
 * The main app component.
 */
class DocumentEditPage : RComponent<DocumentEditPage.Props, DocumentEditPage.State>() {

    interface Props : RProps {
        var session: Session
        var reference: Reference
    }

    interface State : RState {
        var defaultText: String?
    }

    override fun State.init() {
        GlobalScope.launch {
            val text = Session.instance?.download(props.reference.uuid)
            setState {
                defaultText = text
            }
        }
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        val text = state.defaultText ?: "Loading..."
        documentEditor(defaultText = text)
    }

}
