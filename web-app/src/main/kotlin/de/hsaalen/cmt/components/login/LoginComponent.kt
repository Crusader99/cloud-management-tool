package de.hsaalen.cmt.components.login

import react.RBuilder
import react.dom.br
import react.setState

/**
 * Wrapper function to simplify creation of the login component.
 */
fun RBuilder.loginComponent(
    defaultUser: String = "",
    onSubmit: (Credentials) -> Unit = {},
    buttonTitle: String = "Login",
    isEnabled: Boolean = true
) = child(LoginComponent::class) {
    attrs {
        this.defaultUser = defaultUser
        this.onSubmit = onSubmit
        this.buttonTitle = buttonTitle
        this.isEnabled = isEnabled
    }
}

/**
 * A react component for displaying all fields and buttons required for user login.
 */
open class LoginComponent : FormComponent() {

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
                    email = text
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
    }

}
