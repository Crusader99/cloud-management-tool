package de.hsaalen.cmt.components.login

import kotlinx.html.InputType
import react.RBuilder
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
class RegisterComponent(props: Props) : FormComponent(props) {

    /**
     * Called when this form component is rendered by the super class component implementation.
     */
    override fun RBuilder.renderComponents() {
        loginField(
            title = "Full name",
            isEnabled = props.isEnabled,
            defaultText = props.defaultCredentials.fullName,
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
            onTextChange = { text ->
                setState {
                    password = text
                }
            })
        br {}
        loginField(
            title = "Password (repeat)",
            isEnabled = props.isEnabled,
            type = InputType.password,
            onTextChange = { text ->
                setState {
                    password = text
                }
            })
    }

}
