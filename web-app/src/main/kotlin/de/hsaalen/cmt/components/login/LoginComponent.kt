package de.hsaalen.cmt.components.login

import react.RBuilder
import react.dom.br
import react.setState

/**
 * Wrapper function to simplify creation of the login component.
 */
fun RBuilder.loginComponent(
    defaultCredentials: Credentials = Credentials(),
    onSubmit: (Credentials) -> Unit = {},
    buttonTitle: String = "Login",
    isEnabled: Boolean = true
) = child(LoginComponent::class) {
    attrs {
        this.defaultCredentials = defaultCredentials
        this.onSubmit = onSubmit
        this.buttonTitle = buttonTitle
        this.isEnabled = isEnabled
    }
}

/**
 * A react component for displaying all fields and buttons required for user login.
 */
open class LoginComponent(props: Props) : FormComponent(props) {

    /**
     * Called when this form component is rendered by the super class component implementation.
     */
    override fun RBuilder.renderComponents() {
        loginField(
            title = "E-Mail",
            isEnabled = props.isEnabled,
            defaultText = props.defaultCredentials.email,
            onTextChange = { text ->
                setState {
                    email = text
                }
                props.defaultCredentials.email = state.email
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
