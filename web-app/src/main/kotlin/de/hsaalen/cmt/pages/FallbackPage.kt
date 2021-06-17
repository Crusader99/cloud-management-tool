package de.hsaalen.cmt.pages

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import de.hsaalen.cmt.components.dialogs.InputDialogComponent
import de.hsaalen.cmt.components.dialogs.renderInputDialog
import de.hsaalen.cmt.extensions.handleSwitchBackendDialog
import kotlinx.css.*
import react.*
import styled.css

/**
 * React properties of the [FallbackPage] component.
 */
external interface FallbackPageProps : RProps {
    var onRetry: () -> Unit
}

/**
 * Fallback page when backend is not available.
 */
class FallbackPage : RComponent<FallbackPageProps, RState>() {

    /**
     * Reference to create dialog for switching backend a specific url.
     */
    private val refSwitchBackendDialog = createRef<InputDialogComponent>()

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        mBackdrop(open = true, invisible = false) {
            mDialog(open = true) {
                mDialogTitle(text = "", disableTypography = true) {
                    mTypography(text = "Backend unavailable!", variant = MTypographyVariant.h6) { }
                }
                mButton(caption = "Retry", variant = MButtonVariant.contained, color = MColor.primary) {
                    attrs {
                        fullWidth = true
                        onClick = { props.onRetry() }
                    }
                }
            }
        }
        mLink(text = "Switch backend server", underline = MLinkUnderline.always) {
            attrs {
                css {
                    cursor = Cursor.pointer
                    left = 0.px
                    bottom = 0.px
                    position = Position.absolute
                    zIndex = Int.MAX_VALUE
                }
                onClick = { refSwitchBackendDialog.current?.handleSwitchBackendDialog() }
            }
        }
        renderInputDialog(ref = refSwitchBackendDialog)
    }

}
