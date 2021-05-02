package de.hsaalen.cmt.pages

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.js.onClickFunction
import materialui.components.appbar.appBar
import materialui.components.backdrop.backdrop
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonVariant
import materialui.components.circularprogress.circularProgress
import materialui.components.textfield.textField
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import react.*
import react.dom.a
import react.dom.br
import react.dom.h2
import styled.css
import styled.styledDiv

interface Props : RProps {
    var onLogin: () -> Unit
}


interface State : RState {
    var isLoading: Boolean
}

/**
 * Page for user authentication
 */
class LoginPage : RComponent<Props, State>() {

    override fun State.init() {
        isLoading = false
    }

    override fun RBuilder.render() {
        appBar {
            typography {
                attrs {
                    variant = TypographyVariant.h6
                }
                +"Cloud Management Tool"
            }
        }
        h2 { br { } }
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
            textField {
                attrs {
                    label = a { +"Username" }
                    required = true
                    disabled = state.isLoading
                }
            }
            br {}
            textField {
                attrs {
                    type = InputType.password
                    label = a { +"Password" }
                    required = true
                    disabled = state.isLoading
                }
            }
            br {}
            button {
                +"Login"
                attrs {
                    variant = ButtonVariant.contained
                    color = ButtonColor.primary
                    disabled = state.isLoading
                    onClickFunction = {
                        setState {
                            isLoading = true
                        }
                        GlobalScope.launch {
                            delay(2000)
                            props.onLogin()
                        }
                    }
                }
            }
        }
        backdrop {
            attrs {
                open = state.isLoading
                invisible = true
            }
            circularProgress {
            }
        }
    }

}