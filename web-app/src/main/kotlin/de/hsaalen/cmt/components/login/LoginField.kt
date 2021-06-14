package de.hsaalen.cmt.components.login

import com.ccfraser.muirwik.components.MTextFieldProps
import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.mTextField
import kotlinx.html.InputType
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

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
        val title = props.title
        val disable = !props.isEnabled
        val type = if (props.isPasswordField) InputType.password else InputType.text
        val default = props.defaultText
        val margin = MFormControlMargin.none
        mTextField(title, required = true, disabled = disable, defaultValue = default, type = type, margin = margin) {
            attrs {
                onTextChange(props.onTextChange)
            }
        }
    }

    /**
     * Extension helper function to simplify the text change event listener.
     */
    private fun MTextFieldProps.onTextChange(event: (String) -> Unit) {
        onChange = { e ->
            val input = e.target as? HTMLInputElement
            input?.value?.let { text ->
                event(text)
            }
        }
    }


}
