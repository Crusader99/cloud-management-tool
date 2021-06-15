package de.hsaalen.cmt.pages

import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.mBackdrop
import com.ccfraser.muirwik.components.mTypography
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

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
    }

}
