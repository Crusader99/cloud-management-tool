package de.hsaalen.cmt.components.login

import com.ccfraser.muirwik.components.MTextFieldProps
import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.mTextField
import kotlinx.html.InputType
import org.w3c.dom.HTMLInputElement
import react.*

/**
 * Wrapper function to simplify creation of a simple login/password field.
 */
fun RBuilder.loginField(
    title: String = "Unknown field",
    defaultText: String = "",
    onTextChange: (String) -> Unit = {},
    isEnabled: Boolean = true,
    type: InputType = InputType.text,
    onValidate: ValidateEvent = { null },
    autoFocus: Boolean = false,
    ref: RRef? = null
) = child(LoginField::class) {
    attrs {
        this.title = title
        this.defaultText = defaultText
        this.onTextChange = onTextChange
        this.isEnabled = isEnabled
        this.type = type
        this.onValidate = onValidate
        this.autoFocus = autoFocus
        if (ref != null) {
            this.ref = ref
        }
    }
}

/**
 * Lambda that takes the value of the login field as parameter and should return an error message when any error exists.
 * Otherwise null should be returned.
 */
typealias ValidateEvent = (String) -> String?

/**
 * React properties of the [LoginField] component.
 */
external interface LoginFieldProps : RProps {
    var title: String
    var defaultText: String
    var onTextChange: (String) -> Unit
    var isEnabled: Boolean
    var type: InputType
    var onValidate: ValidateEvent
    var autoFocus: Boolean
}

/**
 * React state of the [LoginField] component.
 */
external interface LoginFieldState : RState {
    var currentInputText: String
    var errorMessage: String?
}

/**
 * A component for displaying a simple login/password field.
 */
class LoginField(props: LoginFieldProps) : RComponent<LoginFieldProps, LoginFieldState>(props) {

    override fun LoginFieldState.init(props: LoginFieldProps) {
        currentInputText = props.defaultText
        currentInputText = ""
    }

    /**
     * Called when this input field is rendered.
     */
    override fun RBuilder.render() {
        mTextField(
            label = props.title,
            required = true,
            disabled = !props.isEnabled,
            defaultValue = props.defaultText,
            type = props.type,
            margin = MFormControlMargin.none,
            error = state.errorMessage != null,
            helperText = state.errorMessage,
            autoFocus = props.autoFocus,
        ) {
            attrs {
                onTextChange(::handleTextChange)
                onBlur = { handleValidation() }
            }
        }
    }

    /**
     * Called when ever the user input changes.
     */
    private fun handleTextChange(newText: String) {
        setState {
            this.currentInputText = newText
            if (errorMessage != null) {
                errorMessage = props.onValidate(newText)
            }
        }
        props.onTextChange(newText)
    }

    /**
     * Calls the validation event to check the current user input
     * and display an error when required.
     */
    fun handleValidation() {
        setState {
            errorMessage = props.onValidate(state.currentInputText)
        }
    }

    /**
     * Indicates weather the user typed a valid text. This is calculated by the [handleValidation] function.
     */
    fun isInputTextValid() = state.errorMessage == null
}

/**
 * Extension helper function to simplify the text change event listener.
 */
private fun MTextFieldProps.onTextChange(event: (String) -> Unit) {
    onChange = { e ->
        val input = e.target as? HTMLInputElement
        input?.value?.let { text ->
            event(text)
        }
    }
}
