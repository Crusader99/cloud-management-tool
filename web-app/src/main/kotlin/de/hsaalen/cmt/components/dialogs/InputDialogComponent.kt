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
 * Opens the dialog and suspends until user cancels operation or the user typed in a text. This
 * function will return null when the user cancelled the action otherwise the typed value is returned.
 */
suspend fun InputDialogComponent.show(
    title: String,
    message: String? = null,
    placeholder: String = "",
    button: String = "OK"
): String? = show(title, message, placeholder, buttons = listOf(button))?.inputText

/**
 * Opens the dialog and suspends until user cancels operation or the user typed in a text. This
 * function will return null when the user cancelled the action otherwise the typed value is returned.
 */
suspend fun InputDialogComponent.show(
    title: String,
    message: String? = null,
    placeholder: String = "",
    buttons: List<String>
): InputDialogResult? {
    val newName: InputDialogResult? = suspendCoroutine { continuation ->
        onCloseHandler = { continuation.resume(null) }
        onButtonClickHandler = { index -> continuation.resume(InputDialogResult(state.userInput, index)) }
        setState {
            this.title = title
            this.message = message
            this.placeholder = placeholder
            this.buttons = buttons
            this.isOpen = true
        }
    }
    setState {
        isOpen = false
        userInput = ""
    }
    return newName
}

data class InputDialogResult(val inputText: String, val selectedButtonIndex: Int)

/**
 * React state of the [InputDialogComponent] component.
 */
external interface InputDialogComponentState : RState {
    var title: String
    var message: String?
    var placeholder: String
    var buttons: List<String>

    var isOpen: Boolean
    var userInput: String
}

/**
 * Type alias for the event lambda without any parameters.
 */
private typealias CloseEvent = () -> Unit

/**
 * Type alias for the event lambda without any parameters.
 */
private typealias ButtonClickEvent = (Int) -> Unit

data class Button(
    /**
     * Title to be displayed on this button.
     */
    val title: String,

    /**
     * Optional handler when any button clicked [CloseEvent].
     */
    val onClickHandler: CloseEvent
)

/**
 * A dialog for requesting a text from user.
 */
@JsExport
class InputDialogComponent : RComponent<RProps, InputDialogComponentState>() {

    /**
     * Optional handler for close dialog [CloseEvent].
     */
    var onCloseHandler: CloseEvent? = null

    /**
     * Optional handler when any button clicked.
     */
    var onButtonClickHandler: ButtonClickEvent? = null

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
                val message = state.message
                if (message != null) {
                    for (line in message.lineSequence()) {
                        mTypography(line)
                    }
                    br {}
                }
                mTextField(state.placeholder, autoFocus = true, margin = MFormControlMargin.none, fullWidth = true) {
                    attrs {
                        onTextChange(::onTextChangeHandler)
                        if (state.buttons.size <= 1) { // Enter key only possible when max one button exists
                            onEnterKey { onButtonClickHandler?.invoke(0) }
                        }
                    }
                }
            }
            mDialogActions {
                for ((index, button) in state.buttons.withIndex()) {
                    mButton(caption = button, onClick = { onButtonClickHandler?.invoke(index) })
                }
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

}
