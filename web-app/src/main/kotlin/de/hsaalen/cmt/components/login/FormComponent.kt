package de.hsaalen.cmt.components.login

import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.events.Event
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.attrs
import react.dom.form
import react.dom.h3

/**
 * React properties of the [FormComponent] component.
 */
external interface FormComponentProps : RProps {
    var defaultCredentials: Credentials
    var onSubmit: (Credentials) -> Unit
    var buttonTitle: String
    var isEnabled: Boolean
}

/**
 * React state of the [FormComponent] component.
 */
external interface FormComponentState : RState {
    var fullName: String
    var email: String
    var password: String
    var passwordRepeated: String
}

/**
 * A react component to simplify login/register forms.
 */
abstract class FormComponent(props: FormComponentProps) : RComponent<FormComponentProps, FormComponentState>(props) {

    /**
     * Called when this component is loaded.
     */
    override fun FormComponentState.init(props: FormComponentProps) {
        fullName = props.defaultCredentials.fullName
        email = props.defaultCredentials.email
        password = "" // Reset password
        passwordRepeated = ""
    }

    /**
     * Called when this form component is rendered.
     */
    override fun RBuilder.render() {
        form {
            attrs {
                onSubmitFunction = ::onSubmit
            }
            renderComponents() // This will call render on subclasses
            children()
            h3 {}
            loginButton(props.buttonTitle)
        }
    }

    /**
     * Called to render on subclasses.
     */
    abstract fun RBuilder.renderComponents()

    /**
     * Called when user had entered the username and password.
     */
    protected open fun onSubmit(event: Event) {
        event.preventDefault()
        props.onSubmit(Credentials(state.fullName, state.email, state.password))
    }


}
