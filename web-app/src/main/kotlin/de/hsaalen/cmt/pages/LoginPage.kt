package de.hsaalen.cmt.pages

import com.ccfraser.muirwik.components.MLinkUnderline
import com.ccfraser.muirwik.components.mLink
import de.hsaalen.cmt.components.login.Credentials
import de.hsaalen.cmt.components.login.loginComponent
import de.hsaalen.cmt.components.login.registerComponent
import kotlinx.css.*
import react.*
import react.dom.attrs
import react.dom.h2
import styled.css
import styled.styledDiv

/**
 * React properties of the [LoginPage] component.
 */
external interface LoginPageProps : RProps {
    var onLogin: (Credentials) -> Unit
    var onRegister: (Credentials) -> Unit
    var isEnabled: Boolean
    var lastEmail: String
}

/**
 * React state of the [LoginPage] component.
 */
external interface LoginPageState : RState {
    var showRegistration: Boolean
    var defaultCredentials: Credentials
}

/**
 * Page for user authentication
 */
class LoginPage(props: LoginPageProps) : RComponent<LoginPageProps, LoginPageState>(props) {

    /**
     * Initialize state of the [LoginPage].
     */
    override fun LoginPageState.init(props: LoginPageProps) {
        showRegistration = false
        defaultCredentials = Credentials(email = props.lastEmail)
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
            if (state.showRegistration) {
                registerComponent(
                    defaultCredentials = state.defaultCredentials,
                    onSubmit = props.onRegister,
                    isEnabled = props.isEnabled
                )
            } else {
                loginComponent(
                    defaultCredentials = state.defaultCredentials,
                    onSubmit = props.onLogin,
                    isEnabled = props.isEnabled
                )
            }
        }
        renderLink()
    }

    /**
     * Render the link for switching between registration and login form.
     */
    private fun RBuilder.renderLink() {
        styledDiv {
            attrs {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = Align.flexEnd
                    justifyContent = JustifyContent.right
                }
            }
            val displayText = if (state.showRegistration) "Use existing account" else "Create new account"
            mLink(text = displayText, underline = MLinkUnderline.always) {
                attrs {
                    onClick = {
                        setState {
                            showRegistration = !showRegistration
                        }
                    }
                    css {
                        cursor = Cursor.pointer
                    }
                }
            }
        }
    }

}
