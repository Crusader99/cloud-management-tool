package de.hsaalen.cmt.components

import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.mLink
import com.ccfraser.muirwik.components.mTypography
import kotlinx.css.*
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.br
import styled.css
import styled.styledDiv

/**
 * Wrapper function to simplify creating of the about dialog.
 */
fun RBuilder.aboutDialog(text: String, open: Boolean, onClose: () -> Unit) =
    child(ViewAboutDialog::class) {
        attrs {
            this.text = text
            this.open = open
            this.onClose = onClose
        }
    }

/**
 * A component for displaying a info dialog with links.
 */
class ViewAboutDialog : RComponent<ViewAboutDialog.Props, RState>() {

    interface Props : RProps {
        var text: String
        var open: Boolean
        var onClose: () -> Unit
    }

    /**
     * Called when this dialog is rendered.
     */
    override fun RBuilder.render() {
        mDialog(open = props.open, onClose = { _, _ -> props.onClose() }) {
            mDialogTitle(text = "") {
                attrs {
                    disableTypography = true
                }
                mTypography(text = "About this software", variant = MTypographyVariant.h6) { }
                styledDiv {
                    css {
                        position = Position.absolute
                        right = LinearDimension("1px")
                        top = LinearDimension("1px")
                    }
                    mIconButton(iconName = "close", onClick = { props.onClose() })
                }
            }
            styledDiv {
                css {
                    margin = "0px"
                    padding = "15px"
                }
                renderTextContent()
            }
        }
    }

    /**
     * Render the text content of the dialog including support for https links
     */
    private fun RBuilder.renderTextContent() {
        for (line in props.text.lines()) {
            if (line.isBlank()) {
                br {}
            } else {
                mTypography {
                    if ("https://" in line) {
                        // Parse https link from text line
                        val link = "https://" + line.substringAfter("https://")
                            .substringBefore(" ")
                            .substringBefore(")")
                        mLink(text = line, hRef = link)
                        br {}
                    } else {
                        mTypography(
                            text = line,
                            gutterBottom = true
                        )
                    }
                }
            }
        }
    }

}
