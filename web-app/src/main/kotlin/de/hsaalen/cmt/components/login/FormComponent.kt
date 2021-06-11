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
 * A react component to simplify login/register forms.
 */
abstract class FormComponent(props: Props) : RComponent<FormComponent.Props, FormComponent.State>(props) {

    interface Props : RProps {
        var defaultCredentials: Credentials
        var onSubmit: (Credentials) -> Unit
        var buttonTitle: String
        var isEnabled: Boolean
    }

    interface State : RState {
        var fullName: String
        var email: String
        var password: String
    }

    /**
     * Called when this component is loaded.
     */
    override fun State.init(props: Props) {
        fullName = props.defaultCredentials.fullName
        email = props.defaultCredentials.email
        password = "" // Reset password
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
    private fun onSubmit(event: Event) {
        event.preventDefault()
        props.onSubmit(Credentials(state.fullName, state.email, state.password))
    }


}
