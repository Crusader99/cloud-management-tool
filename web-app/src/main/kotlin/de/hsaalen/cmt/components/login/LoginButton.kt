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
 * React properties for this [LoginButton] component.
 */
external interface LoginButtonProps : RProps {
    var title: String
}

/**
 * A react component for displaying a simple login button which can be used in a form with submit event.
 */
@JsExport
class LoginButton : RComponent<LoginButtonProps, RState>() {

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
        get() = ButtonType.values().first { it.realValue == asDynamic().type.toString() }
        set(value) {
            asDynamic().type = value.realValue
        }

}
