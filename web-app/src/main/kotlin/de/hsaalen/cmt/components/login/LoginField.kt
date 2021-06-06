package de.hsaalen.cmt.components.login

import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import materialui.components.textfield.textField
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a

/**
 * Wrapper function to simplify creation of a simple login/password field.
 */
fun RBuilder.loginField(
    title: String = "Unknown field",
    defaultText: String = "",
    onTextChange: (String) -> Unit = {},
    isEnabled: Boolean = true,
    isPasswordField: Boolean = false
) = child(LoginField::class) {
    attrs {
        this.title = title
        this.defaultText = defaultText
        this.onTextChange = onTextChange
        this.isEnabled = isEnabled
        this.isPasswordField = isPasswordField
    }
}

/**
 * A component for displaying a simple login/password field.
 */
class LoginField : RComponent<LoginField.Props, RState>() {

    interface Props : RProps {
        var title: String
        var defaultText: String
        var onTextChange: (String) -> Unit
        var isEnabled: Boolean
        var isPasswordField: Boolean
    }

    /**
     * Called when this input field is rendered.
     */
    override fun RBuilder.render() {
        textField {
            attrs {
                label = a { +props.title }
                required = true
                disabled = !props.isEnabled
                onTextChange(props.onTextChange)
                defaultValue(props.defaultText)
                if (props.isPasswordField) {
                    type = InputType.password
                }
            }
        }
    }

    /**
     * Extension helper function to simplify the text change event listener.
     */
    private fun CommonAttributeGroupFacade.onTextChange(event: (String) -> Unit) {
        onChangeFunction = { e ->
            val input = e.target as? HTMLInputElement
            input?.value?.let { text ->
                event(text)
            }
        }
    }


}
