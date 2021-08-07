package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.ViewAppBar
import de.hsaalen.cmt.components.documenteditor.AceEngine
import de.hsaalen.cmt.components.documenteditor.DiffCalculator
import de.hsaalen.cmt.components.documenteditor.Engine
import de.hsaalen.cmt.components.documenteditor.aceEditor
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.extensions.launch
import de.hsaalen.cmt.network.dto.objects.LineChangeMode
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.rsocket.DocumentChangeDto
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateRemoveDto
import de.hsaalen.cmt.network.session.Session
import kotlinext.js.jsObject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.css.properties.BoxShadows
import mu.KotlinLogging
import react.*
import styled.css
import styled.injectGlobal

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
    var isReadOnly: Boolean
    var value: String
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
     * The engine for handling text changes.
     */
    private val engine: Engine = AceEngine {
        editor
    }

    /**
     * Output channel for changed that will be sent to server.
     */
    private var channelSend = Channel<DocumentChangeDto>()

    private var editor: dynamic = null

    /**
     * Register events for this component.
     */
    private val events = GlobalEventDispatcher.createBundle(this) {
        register(::onRemovedReference)
        launch {
            diffCalculator.setText("")
            Session.instance?.modifyDocument(props.reference.uuid, channelSend)?.collect { event ->
                onDocumentChangedRemote(event)
            }
        }
    }

    /**
     * Initialize state of the [ViewAppBar].
     */
    override fun DocumentEditPageState.init() {
        isReadOnly = false
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
        val editorWidth = 100.pct
        val editorHeight = 90.pct

        val marker: dynamic = jsObject {
            startRo = 0
            startCol = 2
            endRow = 1
            endCol = 20
            className = "error-marker"
            type = "background"
        }

        val annotation: dynamic = jsObject {
            row = 0
            column = 2
            type = "error"
            text = "A error"
        }


        val styles = CSSBuilder(allowClasses = false).apply {
            ".error" {
                position = Position.absolute
                backgroundColor = Color.green

                hover {
                    backgroundColor = Color.red
                }
            }
        }

        injectGlobal(styles.toString())
//        styledDiv {
//            css {
//                +MarkerStyle.
//            }
        aceEditor {
            css {
                width = editorWidth
                height = editorHeight
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
                value = state.value
                width = editorWidth.toString()
                height = editorHeight.toString()
                wrapEnabled = true
                showPrintMargin = false
                annotations = arrayOf(annotation)
                markers = arrayOf(marker)
                readOnly = state.isReadOnly
                onLoad = {
                    logger.info { "Injected editor instance" }
                    editor = it
                }
                onInput = ::onTextChanged
                onChange = { newText, event ->
                    logger.info { "Editor: onChange" }
                    setState {
                        value = newText
                    }
                }
            }
        }

//        styledTextarea {
//            css {
//                width = 100.pct
//                height = 90.pct
//                top = 64.px
//                left = 0.px
//                right = 0.px
//                bottom = 0.px
//                position = Position.fixed
//                resize = Resize.none
//                outline = Outline.none
//                boxShadow = BoxShadows.none
//                border = "0"
//            }
//            attrs {
//                ref = textarea
//                onInputFunction = { onTextChanged(engine.text) }
//                autoFocus = true
//            }
//        }
    }

    /**
     * Called after every key the user pressed.
     */
    private fun onTextChanged() {
        logger.info { "Editor: onInput" }
        coroutines.launch {
            try {
                setState {
                    isReadOnly = true
                }
                diffCalculator.findChangedLines(state.value)
            } finally {
                setState {
                    isReadOnly = false
                }
            }
        }
    }

    /**
     * Called when any line change detected that has to be transmitted to server.
     */
    private suspend fun onDocumentChangedLocal(lineNumber: Int, lineContent: String, changeMode: LineChangeMode) {
        channelSend.send(DocumentChangeDto(props.reference.uuid, lineNumber, lineContent, changeMode))
    }

    /**
     * Called when the document has been changed from another client instance
     * and has to be synchronized with the local client.
     */
    private fun onDocumentChangedRemote(dto: DocumentChangeDto) {
        if (dto.uuid != props.reference.uuid) {
            return
        }
        logger.info { "Received text change: $dto" }
        val lineContentDecrypted = dto.lineContent
        when (dto.lineChangeMode) {
            LineChangeMode.MODIFY -> engine.modifyLine(dto.lineNumber, lineContentDecrypted)
            LineChangeMode.ADD -> engine.addLine(dto.lineNumber, lineContentDecrypted)
            LineChangeMode.DELETE -> engine.deleteLine(dto.lineNumber)
        }
        val newText = engine.text
        diffCalculator.setText(newText)
        setState {
            value = newText
        }
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

//object MarkerStyle : StyleSheet("ComponentStyles") {
//    // Example of an ".element:hover" selector
//    val error by css {
//        position = Position.absolute
//        backgroundColor = Color.green
//
//        hover {
//            backgroundColor = Color.red
//        }
//    }
//}
