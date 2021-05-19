package de.hsaalen.cmt.components

import com.ccfraser.muirwik.components.mTooltip
import de.hsaalen.cmt.SoftwareInfo
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
import org.w3c.dom.events.Event
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
        var isAboutDialogOpen: Boolean
    }

    override fun State.init() {
        isDrawerVisible = false
        isAboutDialogOpen = false
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        div(props.rootStyle) {
            appBar {
                toolbar {
                    iconButton {
                        attrs {
                            color = ButtonColor.inherit
                            onClickFunction = {
                                setState {
                                    // Show drawer on side
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
                        mTooltip("Logout") {
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
                }
                drawer {
                    attrs {
                        anchor = DrawerAnchor.left
                        open = state.isDrawerVisible
                        onClose = {
                            setState {
                                // Hide drawer on side
                                isDrawerVisible = false
                            }
                        }
                    }
                    renderDrawer()
                }
            }
        }
        h2 { br { } }
        aboutDialog(
            text = SoftwareInfo.description,
            open = state.isAboutDialogOpen,
            onClose = ::onCloseAboutDialog
        )
    }

    /**
     * Render only the drawer menu on the left display side.
     */
    private fun RBuilder.renderDrawer() {
        div {
            list {
                repeat(5) { index ->
                    listButton("Test $index") {}
                }
                listButton("About") {
                    setState {
                        isAboutDialogOpen = true
                    }
                }
            }
        }
    }

    /**
     * Extension function for creating a list button which is intended to be used in a drawer menu.
     */
    private fun RBuilder.listButton(title: String, onClick: (Event) -> Unit) {
        listItem {
            attrs {
                button = true
                onClickFunction = onClick
            }
            listItemText {
                attrs {
                    primary = a {
                        +title
                    }
                }
            }
        }
    }

    /**
     * Called when the about dialog is closed by the user.
     */
    private fun onCloseAboutDialog() {
        setState {
            isAboutDialogOpen = false
        }
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
