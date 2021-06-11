package de.hsaalen.cmt.components.login

import com.ccfraser.muirwik.components.MColor
import com.ccfraser.muirwik.components.button.MButtonProps
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import kotlinx.html.ButtonType
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
private class LoginButton : RComponent<Props, RState>() {

    /**
     * Called when this button component is rendered.
     */
    override fun RBuilder.render() {
        mButton(caption = props.title, variant = MButtonVariant.contained, color = MColor.primary) {
            attrs {
                type = ButtonType.submit
                fullWidth = true
            }
        }
    }

    /**
     * Extension function for accessing button type.
     */
    private var MButtonProps.type: ButtonType
        get() = asDynamic().type as ButtonType
        set(value) {
            asDynamic().type = value
        }

}

/**
 * Properties for this button
 */
external interface Props : RProps {
    var title: String
}
