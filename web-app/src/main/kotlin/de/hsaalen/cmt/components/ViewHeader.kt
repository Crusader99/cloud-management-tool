package de.hsaalen.cmt.components

import demo.components.appsearch.appSearch
import demo.components.header.HeaderProps
import demo.components.header.rootStyle
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
import react.RBuilder
import react.RComponent
import react.RState
import react.dom.a
import react.dom.div
import react.setState

interface ViewHeaderState : RState {
    var isDrawerVisible: Boolean
}

/**
 * Defines the header of the app which also includes a search box and a button with menu options.
 */
class ViewHeader : RComponent<HeaderProps, ViewHeaderState>() {

    override fun ViewHeaderState.init() {
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
                    appSearch { }
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
    }

    companion object {
        fun render(rBuilder: RBuilder) = with(rBuilder) { styledComponent {} }

        private val styledComponent = withStyles(ViewHeader::class, {
            "root" {
                display = Display.flex
            }
            "grow" {
                flex(1.0, 1.0, FlexBasis.auto)
            }
        })
    }
}
