package de.hsaalen.cmt.components.login

import de.hsaalen.cmt.utils.validateEmailAndGetError
import de.hsaalen.cmt.utils.validateFullNameAndGetError
import de.hsaalen.cmt.utils.validatePasswordAndGetError
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import org.w3c.dom.events.Event
import react.RBuilder
import react.createRef
import react.dom.br
import react.setState

/**
 * Wrapper function to simplify creation of the user registration component.
 */
fun RBuilder.registerComponent(
    defaultCredentials: Credentials = Credentials(),
    onSubmit: (Credentials) -> Unit = {},
    buttonTitle: String = "Register",
    isEnabled: Boolean = true
) = child(RegisterComponent::class) {
    attrs {
        this.defaultCredentials = defaultCredentials
        this.onSubmit = onSubmit
        this.buttonTitle = buttonTitle
        this.isEnabled = isEnabled
    }
}

/**
 * A component for handling user registration.
 */
class RegisterComponent(props: FormComponentProps) : FormComponent(props) {

    /**
     * Reference to repeated password field. Required to notify when first
     * password field changed.
     */
    private val repeatPasswordFieldRef = createRef<LoginField>()

    /**
     * Called when this form component is rendered by the super class component implementation.
     */
    override fun RBuilder.renderComponents() {
        loginField(
            title = "Full name",
            isEnabled = props.isEnabled,
            defaultText = props.defaultCredentials.fullName,
            onValidate = { it.validateFullNameAndGetError() },
            autoFocus = true,
            onTextChange = { text ->
                setState {
                    fullName = text
                }
                props.defaultCredentials.fullName = text
            })
        br {}
        loginField(
            title = "E-Mail",
            isEnabled = props.isEnabled,
            defaultText = props.defaultCredentials.email,
            type = InputType.email,
            onValidate = { it.validateEmailAndGetError() },
            onTextChange = { text ->
                setState {
                    email = text
                }
                props.defaultCredentials.email = text
            })
        br {}
        loginField(
            title = "Password",
            isEnabled = props.isEnabled,
            type = InputType.password,
            onValidate = { it.validatePasswordAndGetError() },
            onTextChange = { text ->
                setState {
                    password = text
                }
                // Also correct error-state of the repeated password field when first password field is now correct.
                repeatPasswordFieldRef.current?.also { repeatPasswordField ->
                    if (!repeatPasswordField.isInputTextValid()) {
                        GlobalScope.launch {
                            repeatPasswordFieldRef.current?.handleValidation()
                        }
                    }
                }
            })
        br {}
        loginField(
            title = "Password (repeat)",
            isEnabled = props.isEnabled,
            type = InputType.password,
            onValidate = { if (it != state.password) "Password not equal" else null },
            ref = repeatPasswordFieldRef,
            onTextChange = { text ->
                setState {
                    passwordRepeated = text
                }
            })
    }

    /**
     * Called when user had entered the username and password.
     */
    override fun onSubmit(event: Event) {
        event.preventDefault()

        // Validate repeated password is correct
        if (state.password == state.passwordRepeated) {
            super.onSubmit(event)
        }
    }

}
