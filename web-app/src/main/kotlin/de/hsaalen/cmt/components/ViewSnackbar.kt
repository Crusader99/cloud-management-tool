package de.hsaalen.cmt.components

import com.ccfraser.muirwik.components.MSnackbarOnCloseReason
import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import com.ccfraser.muirwik.components.lab.alert.mAlert
import com.ccfraser.muirwik.components.mSnackbar
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

/**
 * The snackbar component is a wrapper around the material ui component to simplify event handling.
 */
class ViewSnackbar : RComponent<ViewSnackbar.Props, RState>() {

    /**
     * Holds information to be displayed later in snackbar gui object.
     */
    data class SnackbarInfo(
        val message: String,
        val severity: MAlertSeverity
    ) {
        var isVisible = true
    }

    interface Props : RProps {
        var info: SnackbarInfo?
    }

    /**
     * Called whenever an update is required.
     */
    override fun RBuilder.render() {
        val isVisible = props.info?.isVisible ?: false
        val message = props.info?.message ?: ""
        val severity = props.info?.severity ?: MAlertSeverity.info
        mSnackbar(open = isVisible, autoHideDuration = 4_000, onClose = ::onClose) {
            mAlert(message = message, severity = severity)
        }
    }

    /**
     * Called when snackbar is closed by the user or timeout.
     */
    private fun onClose(event: Event, reason: MSnackbarOnCloseReason) {
        if (event.target == null || reason == MSnackbarOnCloseReason.clickaway) {
            return // Ignore clickaway
        }

        // Used props to allow isVisible to be changed on caller side
        props.info?.isVisible = false
        forceUpdate() // Required to ensure snackbar is closed
    }

}
