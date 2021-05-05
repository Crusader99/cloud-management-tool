package de.hsaalen.cmt.components

import de.hsaalen.cmt.views.components.documenteditor.DocumentEditor
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

/**
 * Wrapper function to simplify creation of this react component.
 */
fun RBuilder.documentEditor(defaultText: String) =
    child(ViewDocumentEditor::class) {
        attrs {
            this.defaultText = defaultText
        }
    }

/**
 * A React component for editing documents live with other user users.
 */
class ViewDocumentEditor : RComponent<ViewDocumentEditor.Props, RState>() {

    interface Props : RProps {
        var defaultText: String
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        canvasRenderer(DocumentEditor(props.defaultText))
    }

}
