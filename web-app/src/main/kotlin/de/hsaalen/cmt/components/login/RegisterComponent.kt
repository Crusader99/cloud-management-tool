package de.hsaalen.cmt.components.login

import react.RBuilder
import react.dom.br
import react.setState

/**
 * Wrapper function to simplify creation of the user registration component.
 */
fun RBuilder.registerComponent(
    defaultUser: String = "",
    onSubmit: (Credentials) -> Unit = {},
    buttonTitle: String = "Register",
    isEnabled: Boolean = true
) = child(RegisterComponent::class) {
    attrs {
        this.defaultUser = defaultUser
        this.onSubmit = onSubmit
        this.buttonTitle = buttonTitle
        this.isEnabled = isEnabled
    }
}

/**
 * A component for handling user registration.
 */
class RegisterComponent : FormComponent() {

    /**
     * Called when this form component is rendered by the super class component implementation.
     */
    override fun RBuilder.renderComponents() {
        loginField(
            title = "Username",
            isEnabled = props.isEnabled,
            defaultText = props.defaultUser,
            onTextChange = { text ->
                setState {
                    username = text
                }
            })
        br {}
        loginField(
            title = "Password",
            isEnabled = props.isEnabled,
            isPasswordField = true,
            onTextChange = { text ->
                setState {
                    password = text
                }
            })
        br {}
        loginField(
            title = "Password (repeat)",
            isEnabled = props.isEnabled,
            isPasswordField = true,
            onTextChange = { text ->
                setState {
                    password = text
                }
            })
    }

}
