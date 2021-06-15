package de.hsaalen.cmt.components.dialogs

import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogActions
import com.ccfraser.muirwik.components.dialog.mDialogContent
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.mTextField
import de.hsaalen.cmt.extensions.onEnterKey
import de.hsaalen.cmt.extensions.onTextChange
import react.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Wrapper function to simplify creating of the [DialogCreateReference] dialog.
 */
fun RBuilder.renderReferenceDialog(ref: RReadableRef<DialogCreateReference>) =
    child(DialogCreateReference::class) {
        attrs {
            this.ref = ref
        }
    }

/**
 * React state of the [DialogCreateReference] component.
 */
external interface DialogCreateReferenceState : RState {
    var isOpen: Boolean
    var referenceName: String
}

/**
 * Type alias for the event lambda without any parameters.
 */
private typealias Event = () -> Unit

/**
 * A dialog for requesting a new reference name from user.
 */
class DialogCreateReference : RComponent<RProps, DialogCreateReferenceState>() {

    /**
     * Optional handler for close dialog [Event].
     */
    private var onCloseHandler: Event? = null

    /**
     * Optional handler for create-button clicked [Event].
     */
    private var onCreateHandler: Event? = null

    /**
     * Called the first time when this component is created. Note: The dialog can be shown
     * multiple times using same component.
     */
    override fun DialogCreateReferenceState.init() {
        isOpen = false
        referenceName = ""
    }

    /**
     * Called when this dialog is rendered.
     */
    override fun RBuilder.render() {
        mDialog(open = state.isOpen, onClose = { _, _ -> onCloseHandler?.invoke() }) {
            mDialogTitle(text = "Name for new Reference")
            mDialogContent {
                mTextField("Display name", autoFocus = true, margin = MFormControlMargin.none, fullWidth = true) {
                    attrs {
                        onTextChange(::onTextChangeHandler)
                        onEnterKey { onCreateHandler?.invoke() }
                    }
                }
            }
            mDialogActions {
                mButton(caption = "Create", onClick = { onCreateHandler?.invoke() })
            }
        }
    }

    /**
     * Called when user modifies the text field in the dialog.
     */
    private fun onTextChangeHandler(newText: String) {
        setState {
            referenceName = newText
        }
    }

    /**
     * Opens the dialog and suspends until user cancels operation or new display name for the reference is created. This
     * function will return null when the user cancelled the action otherwise the typed value is returned.
     */
    suspend fun show(): String? {
        val newName: String? = suspendCoroutine { continuation ->
            onCloseHandler = { continuation.resume(null) }
            onCreateHandler = { continuation.resume(state.referenceName) }
            setState { isOpen = true }
        }
        setState {
            isOpen = false
            referenceName = ""
        }
        return newName
    }

}
