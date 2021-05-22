package de.hsaalen.cmt.pages

import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.mBackdrop
import com.ccfraser.muirwik.components.mTypography
import materialui.components.dialogtitle.dialogTitle
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

/**
 * Fallback page when backend is not available.
 */
class FallbackPage : RComponent<FallbackPage.Props, RState>() {

    interface Props : RProps {
        var onRetry: () -> Unit
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        mBackdrop(open = true, invisible = false) {
            mDialog(open = true) {
                dialogTitle {
                    attrs {
                        disableTypography = true
                    }
                    mTypography(text = "Backend unavailable!", variant = MTypographyVariant.h6) { }
                }
            }
        }
    }

}
