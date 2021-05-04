package de.hsaalen.cmt.pages

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.ButtonType
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import materialui.components.backdrop.backdrop
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonVariant
import materialui.components.circularprogress.circularProgress
import materialui.components.textfield.textField
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.a
import react.dom.br
import react.dom.form
import react.dom.h2
import styled.css
import styled.styledDiv

/**
 * Page for user authentication
 */
class LoginPage : RComponent<LoginPage.Props, LoginPage.State>() {

    interface Props : RProps {
        var onLogin: (Credentials) -> Unit
    }

    interface State : RState {
        var isLoading: Boolean
        var username: String
        var password: String
    }

    /**
     * Data which is typed in by the user.
     */
    data class Credentials(val username: String, val password: String)

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
        backdrop {
            attrs {
                open = state.isLoading
                invisible = true
            }
            circularProgress {}
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
            br {}
            button {
                +"Login"
                attrs {
                    variant = ButtonVariant.contained
                    color = ButtonColor.primary
                    disabled = state.isLoading
                    type = ButtonType.submit
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
        GlobalScope.launch {
            props.onLogin(Credentials(state.username, state.password))
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