package de.hsaalen.cmt.components.features

import com.ccfraser.muirwik.components.mBackdrop
import com.ccfraser.muirwik.components.mCircularProgress
import kotlinx.css.zIndex
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css

/**
 * Wrapper function to simplify creating of the loading overlay.
 */
fun RBuilder.loadingOverlay(
    isLoading: Boolean = true,
) = child(ViewLoadingOverlay::class) {
    attrs {
        this.isLoading = isLoading
    }
}

/**
 * React properties of the [ViewLoadingOverlay] component.
 */
external interface ViewLoadingOverlayProps : RProps {
    var isLoading: Boolean
}

/**
 * An overlay component for a loading animation.
 */
@JsExport
class ViewLoadingOverlay : RComponent<ViewLoadingOverlayProps, RState>() {

    /**
     * Called when this overlay component is rendered.
     */
    override fun RBuilder.render() {
        mBackdrop(open = props.isLoading, invisible = false) {
            css {
                zIndex = Int.MAX_VALUE
            }
            mCircularProgress { }
        }
    }

}
