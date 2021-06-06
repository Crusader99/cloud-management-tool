package de.hsaalen.cmt.components.features

import kotlinx.css.Display
import kotlinx.css.display
import kotlinx.html.InputType
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.input
import styled.css
import styled.styledDiv

fun fileUploadDialog(onFileSelected: (String, ByteArray) -> Unit) {
 // TODO: implement
}

class FileUploadDialog : RComponent<FileUploadDialog.Props, RState>() {

    interface Props : RProps {
        var isOpen : Boolean
        var onFileSelected: (String, ByteArray) -> Unit
    }

    /**
     * Called when this overlay component is rendered.
     */
    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.none
            }
            input(InputType.file) {
            }
        }
    }

}
