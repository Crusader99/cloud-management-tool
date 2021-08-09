package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.documenteditor.DiffCalculator
import de.hsaalen.cmt.components.documenteditor.Engine
import de.hsaalen.cmt.components.documenteditor.TextareaEngine
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.extensions.launch
import de.hsaalen.cmt.network.dto.objects.LineChangeMode
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.rsocket.DocumentChangeDto
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateRemoveDto
import de.hsaalen.cmt.network.session.Session
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.css.properties.BoxShadows
import kotlinx.html.js.onInputFunction
import mu.KotlinLogging
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
    var onExit: () -> Unit
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
     * Local logger instance for this [DocumentEditPage].
     */
    private val logger = KotlinLogging.logger("DocumentEditPage")

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
     * Output channel for changed that will be sent to server.
     */
    private var channelSend = Channel<DocumentChangeDto>()

    /**
     * Register events for this component.
     */
    private val events = GlobalEventDispatcher.createBundle(this) {
        register(::onRemovedReference)
        launch {
            val text = ""
            diffCalculator.setText(text)
            engine.text = text
            setState {
                defaultText = text
            }
            Session.instance?.modifyDocument(props.reference.uuid, channelSend)?.collect { event ->
                onDocumentChangedRemote(event)
            }
        }
    }

    /**
     * Remove registered event handlers.
     */
    override fun componentWillUnmount() {
        events.unregisterAll()
        channelSend.close()
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        styledTextarea {
            css {
                width = 100.pct
                height = 90.pct
                top = 64.px
                left = 0.px
                right = 0.px
                bottom = 0.px
                position = Position.fixed
                resize = Resize.none
                outline = Outline.none
                boxShadow = BoxShadows.none
                border = "0"
            }
            attrs {
                ref = textarea
                onInputFunction = { onTextChanged(engine.text) }
                autoFocus = true
            }
        }
    }

    /**
     * Called after every key the user pressed.
     */
    private fun onTextChanged(newText: String) {
        coroutines.launch {
            diffCalculator.findChangedLines(newText)
        }
    }

    /**
     * Called when any line change detected that has to be transmitted to server.
     */
    private suspend fun onDocumentChangedLocal(lineNumber: Int, lineContent: String, changeMode: LineChangeMode) {
        val dto = DocumentChangeDto(props.reference.uuid, lineNumber, lineContent, changeMode)
        try {
            textarea.current?.readOnly = true
            channelSend.send(dto)
        } finally {
            textarea.current?.readOnly = false
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
        logger.debug { "Received text change: $dto" }
        val lineContentDecrypted = dto.lineContent
        when (dto.lineChangeMode) {
            LineChangeMode.MODIFY -> engine.modifyLine(dto.lineNumber, lineContentDecrypted)
            LineChangeMode.ADD -> engine.addLine(dto.lineNumber, lineContentDecrypted)
            LineChangeMode.DELETE -> engine.deleteLine(dto.lineNumber)
        }
        diffCalculator.setText(engine.text)
    }

    /**
     * Event called by server after a reference got deleted.
     */
    private fun onRemovedReference(ref: ReferenceUpdateRemoveDto) {
        if (ref.uuid != props.reference.uuid) {
            return
        }
        // The active open document reference was removed so close editor view
        props.onExit()
    }

}
