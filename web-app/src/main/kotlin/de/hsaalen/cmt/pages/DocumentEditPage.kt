package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.documenteditor.DiffCalculator
import de.hsaalen.cmt.components.documenteditor.Engine
import de.hsaalen.cmt.components.documenteditor.TextareaEngine
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.network.dto.objects.LineChangeMode
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.network.session.Session
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.js.onInputFunction
import org.w3c.dom.HTMLTextAreaElement
import react.*
import react.dom.attrs
import styled.css
import styled.styledTextarea

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
@JsExport
class DocumentEditPage : RComponent<DocumentEditPageProps, DocumentEditPageState>() {
    /**
     * Algorithm that detects changes in document
     */
    private var diffCalculator = DiffCalculator(::onDocumentChangedLocal)

    /**
     * Reference to the text area element.
     */
    private val textarea = createRef<HTMLTextAreaElement>()

    /**
     * The engine for handling text changes.
     */
    private val engine: Engine = TextareaEngine(textarea)

    /**
     * Initialize state of the [DocumentEditPage].
     */
    override fun DocumentEditPageState.init() {
        coroutines.launch {
            val text = Session.instance?.download(props.reference.uuid) ?: return@launch
            diffCalculator.setText(text)
            engine.text = text
            setState {
                defaultText = text
            }

            GlobalEventDispatcher.register(::onDocumentChangedRemote)
        }
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
        styledTextarea {
            css {
                width = 100.pct
                height = 100.pct
                top = 64.px
                left = 0.px
                right = 0.px
                bottom = 0.px
                position = Position.fixed
                resize = Resize.none
            }
            attrs {
                ref = textarea
                onInputFunction = { onTextChanged(engine.text) }
            }
        }
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
    private fun onDocumentChangedLocal(lineNumber: Int, lineContent: String, changeMode: LineChangeMode) {
        val dto = DocumentChangeDto(props.reference.uuid, lineNumber, lineContent, changeMode)
        coroutines.launch {
            Session.instance?.liveTextEdit(dto)
        }
    }

    /**
     * Called when the document has been changed from another client instance
     * and has to be synchronized with the local client.
     */
    private fun onDocumentChangedRemote(dto: DocumentChangeDto) {
        if (dto.uuid != props.reference.uuid) {
            return
        }
        println("Received text change: $dto")
        val lineContentDecrypted = dto.lineContentEncrypted
        when (dto.lineChangeMode) {
            LineChangeMode.MODIFY -> engine.modifyLine(dto.lineNumber, lineContentDecrypted)
            LineChangeMode.ADD -> engine.addLine(dto.lineNumber, lineContentDecrypted)
            LineChangeMode.DELETE -> engine.deleteLine(dto.lineNumber)
        }
    }

}
