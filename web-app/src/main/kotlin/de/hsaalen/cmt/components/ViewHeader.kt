package de.hsaalen.cmt.components

import kotlinx.css.Display
import kotlinx.css.FlexBasis
import kotlinx.css.display
import kotlinx.css.flex
import kotlinx.html.js.onClickFunction
import materialui.components.appbar.appBar
import materialui.components.button.enums.ButtonColor
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import materialui.components.tab.tab
import materialui.components.tabs.tabs
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.withStyles
import react.*
import react.dom.a
import react.dom.br
import react.dom.div
import react.dom.h2

/**
 * Defines the header of the app which also includes a search box and a button with menu options.
 */
class ViewHeader : RComponent<ViewHeader.Props, ViewHeader.State>() {

    interface Props : RProps {
        var onLogout: () -> Unit
        var isLoggedIn: Boolean
        val classes: dynamic
    }

    private val Props.rootStyle: String
        get() = classes["root"] as String

    interface State : RState {
        var isDrawerVisible: Boolean
    }

    override fun State.init() {
        isDrawerVisible = false
    }

    override fun RBuilder.render() {
        div(props.rootStyle) {
            appBar {
                toolbar {
                    iconButton {
                        attrs {
                            color = ButtonColor.inherit
                            onClickFunction = {
                                setState {
                                    isDrawerVisible = true
                                }
                            }
                        }
                        icon {
                            +"menu_icon"
                        }
                    }
                    typography {
                        attrs {
                            variant = TypographyVariant.h6
                        }
                        +"Cloud Management Tool"
                    }
                    if (props.isLoggedIn) {
                        tabs {
                            tab {
                                attrs {
                                    label = a { +"Search results" }
                                }
                            }
                            tab {
                                attrs {
                                    label = a { +"File info" }
                                }
                            }
                            tab {
                                attrs {
                                    label = a { +"File content" }
                                }
                            }
                        }
                        div(props.classes["grow"] as String) {}
                        iconButton {
                            attrs {
                                color = ButtonColor.inherit
                                onClickFunction = { props.onLogout() }
                            }
                            icon {
                                +"logout_icon"
                            }
                        }
                    }
                }
                drawer {
                    attrs {
                        anchor = DrawerAnchor.left
                        open = state.isDrawerVisible
                        onClose = {
                            setState {
                                isDrawerVisible = false
                            }
                        }
                    }
                    div {
                        list {
                            repeat(5) { index ->
                                listItem {
                                    attrs {
                                        button = true
                                    }
                                    listItemText {
                                        attrs {
                                            primary = a {
                                                +"Test $index"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        h2 { br { } }
    }

    companion object {
        val styledComponent = withStyles(ViewHeader::class, {
            "root" {
                display = Display.flex
            }
            "grow" {
                flex(1.0, 1.0, FlexBasis.auto)
            }
        })
    }
}
