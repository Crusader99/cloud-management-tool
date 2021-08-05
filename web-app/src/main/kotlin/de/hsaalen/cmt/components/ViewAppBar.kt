package de.hsaalen.cmt.components

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItem
import com.ccfraser.muirwik.components.list.mListItemText
import de.hsaalen.cmt.SoftwareInfo
import de.hsaalen.cmt.components.dialogs.aboutSoftwareDialog
import de.hsaalen.cmt.events.EventType
import de.hsaalen.cmt.events.launchNotification
import de.hsaalen.cmt.network.session.Session
import kotlinx.browser.window
import kotlinx.css.FlexBasis
import kotlinx.css.flex
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import styled.css
import styled.styledDiv

/**
 * Wrapper function to simplify creation of this [ViewAppBar] react component.
 */
fun RBuilder.appBar(
    isLoggedIn: Boolean,
    drawerMenu: Map<String, (Event) -> Unit>,
) = child(ViewAppBar::class) {
    attrs {
        this.isLoggedIn = isLoggedIn
        this.drawerMenu = drawerMenu
    }
}

/**
 * React props of the [ViewAppBar] component.
 */
external interface ViewAppBarProps : RProps {
    var isLoggedIn: Boolean
    var drawerMenu: Map<String, (Event) -> Unit>
}

/**
 * React state of the [ViewAppBar] component.
 */
external interface ViewAppBarState : RState {
    var isDrawerVisible: Boolean
    var isAboutDialogOpen: Boolean
}

/**
 * Defines the header of the app which also includes a search box and a button with menu options.
 */
@JsExport
class ViewAppBar : RComponent<ViewAppBarProps, ViewAppBarState>() {

    /**
     * Initialize state of the [ViewAppBar].
     */
    override fun ViewAppBarState.init() {
        isDrawerVisible = false
        isAboutDialogOpen = false
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        mAppBar {
            mToolbar {
                mTooltip("Menu") {
                    mIconButton(iconName = "menu_icon", color = MColor.inherit, onClick = {
                        setState {
                            // Show drawer on side
                            isDrawerVisible = true
                        }
                    })
                }
                mTypography(text = "Cloud Management Tool", variant = MTypographyVariant.h6)
                if (props.isLoggedIn) {
                    styledDiv {
                        css {
                            flex(1.0, 1.0, FlexBasis.auto)
                        }
                    }
                    if (window.innerWidth > 500) {  // Only print user name when on large page
                        Session.instance?.userInfo?.let { userInfo ->
                            mTooltip("Logged in as " + userInfo.email) {
                                p { +userInfo.fullName }
                            }
                        }
                    }
                    mTooltip("Logout") {
                        mIconButton(
                            iconName = "logout_icon",
                            color = MColor.inherit,
                            onClick = { launchNotification(EventType.PRE_LOGOUT) }
                        )
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
        h2 { br { } }
        aboutSoftwareDialog(
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
