package de.hsaalen.cmt.pages

import com.ccfraser.muirwik.components.mBackdrop
import com.ccfraser.muirwik.components.mCircularProgress
import kotlinx.css.*
import kotlinx.html.ButtonType
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonVariant
import materialui.components.textfield.textField
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import styled.css
import styled.styledDiv

/**
 * Page for user authentication
 */
class LoginPage : RComponent<LoginPage.Props, LoginPage.State>() {

    /**
     * Data which is typed in by the user.
     */
    data class Credentials(val username: String, val password: String)

    interface Props : RProps {
        var onLogin: (Credentials) -> Unit
    }

    interface State : RState {
        var isLoading: Boolean
        var username: String
        var password: String
    }

    /**
     * Called when this component is loaded.
     */
    override fun State.init() {
        isLoading = false
        username = ""
        password = ""
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        styledDiv {
            attrs {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = Align.center
                    justifyContent = JustifyContent.center
                    border = "1px solid lightgray"
                    padding = "50px"
                }
            }
            h2 {
                +"Authentication"
            }
            renderLoginForm()
        }

        mBackdrop(open = state.isLoading, invisible = false) {
            css {
                zIndex = Int.MAX_VALUE
            }
            mCircularProgress { }
        }
    }

    /**
     * Called by the render function to add the login components.
     */
    private fun RBuilder.renderLoginForm() {
        form {
            attrs {
                onSubmitFunction = ::onSubmit
            }
            textField {
                attrs {
                    label = a { +"Username" }
                    required = true
                    disabled = state.isLoading
                    onTextChange { text ->
                        setState {
                            username = text
                        }
                    }
                }
            }
            br {}
            textField {
                attrs {
                    type = InputType.password
                    label = a { +"Password" }
                    required = true
                    disabled = state.isLoading
                    onTextChange { text ->
                        setState {
                            password = text
                        }
                    }
                }
            }
            h3 {}
            button {
                +"Login"
                attrs {
                    variant = ButtonVariant.contained
                    color = ButtonColor.primary
                    type = ButtonType.submit
                    fullWidth = true
                }
            }
        }
    }

    /**
     * Called when user had entered the username and password.
     */
    private fun onSubmit(event: Event) {
        event.preventDefault()
        setState {
            isLoading = true
        }
        props.onLogin(Credentials(state.username, state.password))
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
