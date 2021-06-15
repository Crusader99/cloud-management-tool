package de.hsaalen.cmt.components.features

import com.ccfraser.muirwik.components.MSnackbarOnCloseReason
import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import com.ccfraser.muirwik.components.lab.alert.mAlert
import com.ccfraser.muirwik.components.mSnackbar
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import react.*
import kotlin.coroutines.CoroutineContext

/**
 * Wrapper function to simplify rendering the [ViewSnackbar] content.
 */
fun RBuilder.renderSnackbar(ref: RReadableRef<ViewSnackbar>) =
    child(ViewSnackbar::class) {
        attrs {
            this.ref = ref
        }
    }

/**
 * Holds information to be displayed later in snackbar gui object.
 */
external interface ViewSnackbarState : RState {
    var message: String
    var severity: MAlertSeverity
    var isVisible: Boolean
}

/**
 * The snackbar component is a wrapper around the material ui component to simplify event handling.
 */
class ViewSnackbar : RComponent<RProps, ViewSnackbarState>() {

    /**
     * Holds the current close job to to allow cancellation. Required for replacing snack bar content.
     */
    private var currentCloseJob: CoroutineContext? = null

    /**
     * Initialize state of the [ViewSnackbar].
     */
    override fun ViewSnackbarState.init() {
        message = ""
        severity = MAlertSeverity.info
        isVisible = false
    }

    /**
     * Called whenever an update is required.
     */
    override fun RBuilder.render() {
        mSnackbar(
            open = state.isVisible,
            onClose = { _, reason -> onClose(reason) }) {
            mAlert(message = state.message, severity = state.severity, onClose = { onClose() })
        }
    }

    /**
     * Called when snackbar is closed by the user or timeout.
     */
    private fun onClose(reason: MSnackbarOnCloseReason? = null) {
        if (reason == MSnackbarOnCloseReason.clickaway) {
            return // Ignore clickaway
        }

        setState {
            isVisible = false
        }
        currentCloseJob?.cancel()
    }

    /**
     * Opens a new snack bar and replaces the previous one when the previous one is still open.
     */
    suspend fun show(message: String, severity: MAlertSeverity, timeoutMs: Long = 4_000) {
        currentCloseJob?.cancel() // Cancel previous snack bar close-after-timeout handler
        setState {
            this.isVisible = true
            this.message = message
            this.severity = severity
        }
        coroutineScope {
            val closeJob = launch {
                delay(timeoutMs)
                setState {
                    isVisible = false
                }
            }
            currentCloseJob = closeJob // Register close job to allow cancellation
            closeJob.join()
        }
    }

}
