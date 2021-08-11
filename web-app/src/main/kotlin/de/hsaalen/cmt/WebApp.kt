package de.hsaalen.cmt

import com.ccfraser.muirwik.components.styles.mStylesProvider
import de.hsaalen.cmt.components.dialogs.InputDialogComponent
import de.hsaalen.cmt.components.dialogs.renderInputDialog
import de.hsaalen.cmt.components.features.ViewSnackbar
import de.hsaalen.cmt.components.features.loadingOverlay
import de.hsaalen.cmt.components.features.renderSnackbar
import de.hsaalen.cmt.components.header.appBar
import de.hsaalen.cmt.events.EventType
import de.hsaalen.cmt.events.GuiOperations
import de.hsaalen.cmt.events.LoginEvent
import de.hsaalen.cmt.events.launchNotification
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.pages.AuthenticationPage
import de.hsaalen.cmt.pages.DocumentEditPage
import de.hsaalen.cmt.pages.FallbackPage
import de.hsaalen.cmt.pages.OverviewPage
import de.hsaalen.cmt.theme.ThemeProvider
import de.hsaalen.cmt.theme.toMuiTheme
import org.w3c.dom.events.Event
import react.*
import react.dom.header

/**
 * React properties of the [WebApp] component.
 */
external interface WebAppState : RState {
    var page: EnumPageType
    var reference: Reference?
    var loadingTasks: Int
}

/**
 * The main app component.
 */
@JsExport
class WebApp : RComponent<RProps, WebAppState>() {

    /**
     * Reference to create dialog for requesting user to type a specific reference name.
     */
    val refInputDialog = createRef<InputDialogComponent>()

    /**
     * Reference to snack bar helper class required to send notifications etc.
     */
    val refSnackBar = createRef<ViewSnackbar>()

    /**
     * Called when this component is loaded.
     */
    override fun WebAppState.init() {
        loadingTasks = 0
        page = EnumPageType.CONNECTING
        reference = null

        GuiOperations.webApp = this@WebApp
        launchNotification(EventType.PRE_RECONNECT)
    }

    /**
     * Called whenever an update is required.
     */
    override fun RBuilder.render() {
        mStylesProvider {
            child(ThemeProvider::class) {
                attrs.theme = Theme.LIGHT.toMuiTheme()
                renderHeader()
                renderInputDialog(refInputDialog)
                renderSnackbar(refSnackBar)
                loadingOverlay(state.loadingTasks > 0)

                when (state.page) {
                    EnumPageType.CONNECTING -> {
                        // Keep empty to print empty page
                    }
                    EnumPageType.UNAVAILABLE -> {
                        child(FallbackPage::class) {
                            attrs {
                                onRetry = { launchNotification(EventType.PRE_RECONNECT) }
                            }
                        }
                    }
                    EnumPageType.AUTHENTICATION -> {
                        // Allow user to login
                        child(AuthenticationPage::class) {
                            attrs {
                                onLogin = { credentials ->
                                    launchNotification(
                                        EventType.PRE_LOGIN,
                                        LoginEvent(credentials, isRegistration = false)
                                    )
                                }
                                onRegister = { credentials ->
                                    launchNotification(
                                        EventType.PRE_LOGIN,
                                        LoginEvent(credentials, isRegistration = true)
                                    )
                                }
                                lastEmail = ""
                                isEnabled = state.loadingTasks <= 0
                            }
                        }
                    }
                    EnumPageType.OVERVIEW -> {
                        val localSession = Session.instance!! // TODO: exception handling
                        // When already logged in
                        child(OverviewPage::class) {
                            attrs {
                                session = localSession
                            }
                        }
                    }
                    EnumPageType.EDIT_DOCUMENT -> {
                        val localSession = Session.instance!! // TODO: exception handling
                        val ref = state.reference!!
                        child(DocumentEditPage::class) {
                            attrs {
                                session = localSession
                                reference = ref
                                onExit = {
                                    setState {
                                        page = EnumPageType.OVERVIEW
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called every time when main render function is called. This method contains code for rendering the app header.
     */
    private fun RBuilder.renderHeader() = header {
        val menuItems = linkedMapOf<String, (Event) -> Unit>()
        if (state.page.isLoggedIn) {
            if (state.page == EnumPageType.EDIT_DOCUMENT) {
                menuItems["Back"] = {
                    setState {
                        page = EnumPageType.OVERVIEW
                    }
                }
            } else if (state.page == EnumPageType.OVERVIEW) {
                menuItems["New document"] = { launchNotification(EventType.PRE_CREATE_NEW_DOCUMENT) }
                menuItems["Import documents"] = { launchNotification(EventType.PRE_DOCUMENT_IMPORT) }
                menuItems["Upload files"] = { launchNotification(EventType.PRE_FILE_UPLOAD) }
            }
        }

        appBar(isLoggedIn = state.page.isLoggedIn, drawerMenu = menuItems)
    }

}


