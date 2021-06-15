package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.canvasRenderer
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.views.components.documenteditor.DocumentEditor
import kotlinx.coroutines.launch
import react.*

/**
 * A React component for editing documents live with other user users.
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
        coroutines.launch {
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
        canvasRenderer(DocumentEditor(text, ::onTextChanged))
    }


    /**
     * Called after every key the user pressed.
     */
    private fun onTextChanged(newText: String) {
        coroutines.launch {
            // TODO: implement
//            val dto = DocumentChangeDto(props.reference.uuid, newText)
//            Session.instance?.liveTextEdit(dto)
        }
    }

}
