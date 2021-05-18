package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.login.Credentials
import de.hsaalen.cmt.components.login.loginComponent
import de.hsaalen.cmt.components.login.registerComponent
import kotlinx.css.*
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.h2
import styled.css
import styled.styledDiv

/**
 * Page for user authentication
 */
class LoginPage : RComponent<LoginPage.Props, RState>() {

    interface Props : RProps {
        var onLogin: (Credentials) -> Unit
        var onRegister: (Credentials) -> Unit
        var showRegistration: Boolean
        var isEnabled: Boolean
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
            if (props.showRegistration) {
                registerComponent(onSubmit = props.onRegister, isEnabled = props.isEnabled)
            } else {
                loginComponent(onSubmit = props.onLogin, isEnabled = props.isEnabled)
            }
        }
    }

}
