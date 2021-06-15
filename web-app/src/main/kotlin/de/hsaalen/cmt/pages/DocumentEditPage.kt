package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.canvasRenderer
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.network.dto.objects.LineChangeMode
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.views.components.documenteditor.DiffCalculator
import de.hsaalen.cmt.views.components.documenteditor.DocumentEditor
import kotlinx.coroutines.launch
import react.*

/**
 * React properties of the [DocumentEditPage] component.
 */
external interface DocumentEditPageProps : RProps {
    var session: Session
    var reference: Reference
}

/**
 * React state of the [DocumentEditPage] component.
 */
external interface DocumentEditPageState : RState {
    var defaultText: String?
}

/**
 * A React component for editing documents live with other user users.
 */
class DocumentEditPage : RComponent<DocumentEditPageProps, DocumentEditPageState>() {
    /**
     * Algorithm that detects changes in document
     */
    private var diffCalculator = DiffCalculator(::onLineChanged)

    /**
     * Initialize state of the [DocumentEditPage].
     */
    override fun DocumentEditPageState.init() {
        coroutines.launch {
            val text = Session.instance?.download(props.reference.uuid) ?: return@launch
            diffCalculator.setText(text)
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
        diffCalculator.findChangedLines(newText)
    }

    /**
     * Called when any line change detected that has to be transmitted to server.
     */
    private fun onLineChanged(lineNumber: Int, lineContent: String, changeMode: LineChangeMode) {
        val dto = DocumentChangeDto(props.reference.uuid, lineNumber, lineContent, changeMode)
        coroutines.launch {
            Session.instance?.liveTextEdit(dto)
        }
    }

}
