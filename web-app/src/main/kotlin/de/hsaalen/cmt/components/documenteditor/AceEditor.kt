package de.hsaalen.cmt.components.documenteditor

import com.ccfraser.muirwik.components.createStyled
import com.ccfraser.muirwik.components.setStyledPropsAndRunHandler
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RState
import styled.StyledHandler
import styled.StyledProps

@JsModule("react-ace")
private external val reactAce: dynamic

@Suppress("UnsafeCastFromDynamic")
private val buttonComponent: RComponent<AceEditorProps, RState> = reactAce.default

/**
 * React props of the AceEditor component.
 * See https://github.com/securingsincity/react-ace/blob/master/docs/Ace.md
 */
external interface AceEditorProps : StyledProps {
    var mode: String

    /**
     * Theme to use.
     */
    var theme: String

    var onLoad: (dynamic) -> Unit
    var onChange: (String, Event) -> Unit
    var onInput: () -> Unit
    var onCursorChange: (String, Event) -> Unit
    var name: String

    /**
     * Value you want to populate in the code highlighter.
     */
    var value: String

    /**
     * Default value of the editor.
     */
    var defaultValue: String

    var placeholder: String

    /**
     * Show gutter. Default: true
     */
    var showGutter: Boolean

    /**
     * Show print margin. Default: true
     */
    var showPrintMargin: Boolean

    /**
     * Wrapping lines. Default: false
     */
    var wrapEnabled: Boolean

    /**
     * Make the editor read only. Default: false
     */
    var readOnly: Boolean

    var height: String
    var width: String

    var annotations: Array<dynamic>
    var markers: Array<dynamic>
}

fun RBuilder.aceEditor(handler: StyledHandler<AceEditorProps>) = createStyled(buttonComponent) {
    setStyledPropsAndRunHandler(null, handler)
}
