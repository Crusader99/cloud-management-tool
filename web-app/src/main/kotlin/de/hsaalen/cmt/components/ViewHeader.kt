package de.hsaalen.cmt.components

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemText
import de.hsaalen.cmt.SoftwareInfo
import kotlinx.css.Display
import kotlinx.css.FlexBasis
import kotlinx.css.display
import kotlinx.css.flex
import org.w3c.dom.events.Event
import react.*
import react.dom.a
import react.dom.br
import react.dom.div
import react.dom.h2
import styled.css
import styled.styledDiv

/**
 * Defines the header of the app which also includes a search box and a button with menu options.
 */
class ViewHeader : RComponent<ViewHeader.Props, ViewHeader.State>() {

    interface Props : RProps {
        var onLogout: () -> Unit
        var isLoggedIn: Boolean
        var drawerMenu: Map<String, (Event) -> Unit>
    }

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
        styledDiv {
            css {
                display = Display.flex
            }
            mAppBar {
                mToolbar {
                    mIconButton(iconName = "menu_icon", color = MColor.inherit, onClick = {
                        setState {
                            // Show drawer on side
                            isDrawerVisible = true
                        }
                    })
                    mTypography(text = "Cloud Management Tool", variant = MTypographyVariant.h6)
                    if (props.isLoggedIn) {
                        // TODO: fix or remove
//                    tabs {
//                        tab {
//                            attrs {
//                                label = a { +"Search results" }
//                            }
//                        }
//                        tab {
//                            attrs {
//                                label = a { +"File info" }
//                            }
//                        }
//                        tab {
//                            attrs {
//                                label = a { +"File content" }
//                            }
//                        }
//                    }
                        styledDiv {
                            css {
                                flex(1.0, 1.0, FlexBasis.auto)
                            }
                        }
                        mTooltip("Logout") {
                            mIconButton(
                                iconName = "logout_icon",
                                color = MColor.inherit,
                                onClick = { props.onLogout() })
                        }
                    }
                }
                mDrawer(open = state.isDrawerVisible, anchor = MDrawerAnchor.left, onClose = {
                    setState {
                        // Hide drawer on side
                        isDrawerVisible = false
                    }
                }) {
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
            mList {
                for ((text, onClick) in props.drawerMenu) {
                    listButton(text, onClick)
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
    private fun RBuilder.listButton(title: String, onClickHandler: (Event) -> Unit) {
        mListItem(button = true, onClick = onClickHandler) {
            mListItemText {
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
            isDrawerVisible = false
        }
    }
}
