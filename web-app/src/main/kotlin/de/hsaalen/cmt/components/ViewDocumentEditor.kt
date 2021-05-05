package de.hsaalen.cmt.components

import de.hsaalen.cmt.views.TestView
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

/**
 * Wrapper function to simplify creation of this react component.
 */
fun RBuilder.documentEditor(text: String) =
    child(ViewDocumentEditor::class) {
        attrs {
            this.text = text
        }
    }

/**
 * A React component for editing documents live with other user users.
 */
class ViewDocumentEditor : RComponent<ViewDocumentEditor.Props, RState>() {

    interface Props : RProps {
        var text: String
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        canvasRenderer(TestView())
    }

}
