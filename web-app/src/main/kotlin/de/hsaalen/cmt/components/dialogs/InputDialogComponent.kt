package de.hsaalen.cmt.components.dialogs

import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.dialog.mDialogActions
import com.ccfraser.muirwik.components.dialog.mDialogContent
import com.ccfraser.muirwik.components.dialog.mDialogTitle
import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.mTextField
import com.ccfraser.muirwik.components.mTypography
import de.hsaalen.cmt.extensions.onEnterKey
import de.hsaalen.cmt.extensions.onTextChange
import react.*
import react.dom.br
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Wrapper function to simplify creating of the [InputDialogComponent] dialog.
 */
fun RBuilder.renderInputDialog(ref: RReadableRef<InputDialogComponent>) =
    child(InputDialogComponent::class) {
        attrs {
            this.ref = ref
        }
    }

/**
 * React state of the [InputDialogComponent] component.
 */
external interface InputDialogComponentState : RState {
    var title: String
    var message: String?
    var placeholder: String
    var button: String

    var isOpen: Boolean
    var userInput: String
}

/**
 * Type alias for the event lambda without any parameters.
 */
private typealias Event = () -> Unit

/**
 * A dialog for requesting a text from user.
 */
class InputDialogComponent : RComponent<RProps, InputDialogComponentState>() {

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
    override fun InputDialogComponentState.init() {
        isOpen = false
        userInput = ""
    }

    /**
     * Called when this dialog is rendered.
     */
    override fun RBuilder.render() {
        mDialog(open = state.isOpen, onClose = { _, _ -> onCloseHandler?.invoke() }) {
            mDialogTitle(text = state.title)
            mDialogContent {
                if (state.message != null) {
                    mTypography(state.message)
                    br {}
                }
                mTextField(state.placeholder, autoFocus = true, margin = MFormControlMargin.none, fullWidth = true) {
                    attrs {
                        onTextChange(::onTextChangeHandler)
                        onEnterKey { onCreateHandler?.invoke() }
                    }
                }
            }
            mDialogActions {
                mButton(caption = state.button, onClick = { onCreateHandler?.invoke() })
            }
        }
    }

    /**
     * Called when user modifies the text field in the dialog.
     */
    private fun onTextChangeHandler(newText: String) {
        setState {
            userInput = newText
        }
    }

    /**
     * Opens the dialog and suspends until user cancels operation or the user typed in a text. This
     * function will return null when the user cancelled the action otherwise the typed value is returned.
     */
    suspend fun show(
        title: String,
        message: String? = null,
        placeholder: String = "",
        button: String = "OK"
    ): String? {
        val newName: String? = suspendCoroutine { continuation ->
            onCloseHandler = { continuation.resume(null) }
            onCreateHandler = { continuation.resume(state.userInput) }
            setState {
                this.title = title
                this.message = message
                this.placeholder = placeholder
                this.button = button
                this.isOpen = true
            }
        }
        setState {
            isOpen = false
            userInput = ""
        }
        return newName
    }

}
