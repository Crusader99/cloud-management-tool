package de.hsaalen.cmt.pages

import com.ccfraser.muirwik.components.MLinkUnderline
import com.ccfraser.muirwik.components.mLink
import de.hsaalen.cmt.components.dialogs.InputDialogComponent
import de.hsaalen.cmt.components.dialogs.renderInputDialog
import de.hsaalen.cmt.components.login.Credentials
import de.hsaalen.cmt.components.login.loginComponent
import de.hsaalen.cmt.components.login.registerComponent
import de.hsaalen.cmt.extensions.handleSwitchBackendDialog
import kotlinx.css.*
import react.*
import react.dom.attrs
import react.dom.h2
import styled.css
import styled.styledDiv

/**
 * React properties of the [AuthenticationPage] component.
 */
external interface AuthenticationPageProps : RProps {
    var onLogin: (Credentials) -> Unit
    var onRegister: (Credentials) -> Unit
    var isEnabled: Boolean
    var lastEmail: String
}

/**
 * React state of the [AuthenticationPage] component.
 */
external interface AuthenticationPageState : RState {
    var showRegistration: Boolean
    var defaultCredentials: Credentials
}

/**
 * Page for user authentication
 */
@JsExport
class AuthenticationPage(props: AuthenticationPageProps) :
    RComponent<AuthenticationPageProps, AuthenticationPageState>(props) {

    /**
     * Reference to create dialog for switching backend a specific url.
     */
    private val refSwitchBackendDialog = createRef<InputDialogComponent>()

    /**
     * Initialize state of the [AuthenticationPage].
     */
    override fun AuthenticationPageState.init(props: AuthenticationPageProps) {
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
        renderInputDialog(ref = refSwitchBackendDialog)
    }

    /**
     * Render the link for switching between registration and login form.
     */
    private fun RBuilder.renderLink() {
        styledDiv {
            attrs {
                css {
                    width = 100.pct
                }
            }
            mLink(text = "Switch backend server", underline = MLinkUnderline.always) {
                attrs {
                    css {
                        cursor = Cursor.pointer
                        float = Float.left
                    }
                    onClick = { refSwitchBackendDialog.current?.handleSwitchBackendDialog() }
                }
            }

            val switchPage = if (state.showRegistration) "Use existing account" else "Create new account"
            mLink(text = switchPage, underline = MLinkUnderline.always) {
                attrs {
                    css {
                        cursor = Cursor.pointer
                        float = Float.right
                    }
                    onClick = {
                        setState {
                            showRegistration = !showRegistration
                        }
                    }
                }
            }

        }
    }

}
