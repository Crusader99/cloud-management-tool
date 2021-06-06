package de.hsaalen.cmt.components.login

import kotlinx.html.ButtonType
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonVariant
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

/**
 * Wrapper function to simplify creation of a login button.
 */
fun RBuilder.loginButton(
    title: String = "Button",
) = child(LoginButton::class) {
    attrs {
        this.title = title
    }
}

/**
 * A react component for displaying a simple login button which can be used in a form with submit event.
 */
class LoginButton : RComponent<LoginButton.Props, RState>() {

    interface Props : RProps {
        var title: String
    }

    /**
     * Called when this button component is rendered.
     */
    override fun RBuilder.render() {
        button {
            +props.title
            attrs {
                variant = ButtonVariant.contained
                color = ButtonColor.primary
                type = ButtonType.submit
                fullWidth = true
            }
        }
    }

}
