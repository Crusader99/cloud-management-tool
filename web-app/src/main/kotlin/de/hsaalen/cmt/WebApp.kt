package de.hsaalen.cmt

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import de.hsaalen.cmt.components.ViewHeader
import de.hsaalen.cmt.components.features.ViewSnackbar
import de.hsaalen.cmt.components.features.loadingOverlay
import de.hsaalen.cmt.components.login.Credentials
import de.hsaalen.cmt.network.client.Session
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.pages.DocumentEditPage
import de.hsaalen.cmt.pages.FallbackPage
import de.hsaalen.cmt.pages.LoginPage
import de.hsaalen.cmt.pages.OverviewPage
import kotlinx.coroutines.*
import materialui.styles.themeprovider.themeProvider
import react.*
import react.dom.header

/**
 * The main app component.
 */
class WebApp : RComponent<RProps, WebApp.State>() {
    interface State : RState {
        var snackbar: ViewSnackbar.SnackbarInfo?
        var page: EnumPageType
        var isLoading: Boolean
    }

    /**
     * Called when this component is loaded.
     */
    override fun State.init() = reconnect()


    /**
     * Configures the react state to open the connecting page.
     */
    private fun State.reconnect() {
        Session.instance = null
        snackbar = null
        isLoading = true
        page = EnumPageType.CONNECTING

        GlobalScope.launch {
            try {
                val restoredSession = Session.restore()
                setState {
                    isLoading = false
                    if (restoredSession == null) {
                        // Not logged in
                        page = EnumPageType.AUTHENTICATION
                    } else {
                        // Logged in
                        Session.instance = restoredSession
                        page = EnumPageType.OVERVIEW
                    }
                }
            } catch (ex: ConnectException) {
                delay(2000)
                setState {
                    isLoading = false
                    page = EnumPageType.UNAVAILABLE
                }
            } catch (ex: Exception) {
                // Ignore other errors
            }
        }
    }

    /**
     * Called whenever an update is required.
     */
    override fun RBuilder.render() {
        themeProvider(Themes.LIGHT) {
            header {
                ViewHeader.styledComponent {
                    attrs {
                        isLoggedIn = state.page.isLoggedIn
                        onLogout = ::onLogout
                    }
                }
            }

            child(ViewSnackbar::class) {
                attrs {
                    info = state.snackbar
                }
            }

            when (state.page) {
                EnumPageType.CONNECTING -> {
                    // Keep empty to print empty page
                }
                EnumPageType.UNAVAILABLE -> {
                    child(FallbackPage::class) {
                        attrs {
                            onRetry = ::onReconnect
                        }
                    }
                }
                EnumPageType.AUTHENTICATION -> {
                    // Allow user to login
                    child(LoginPage::class) {
                        attrs {
                            onLogin =
                                { credentials -> onLogin(credentials, isRegistration = false) }
                            onRegister =
                                { credentials -> onLogin(credentials, isRegistration = true) }
                            lastEmail = ""
                            isEnabled = !state.isLoading
                        }
                    }
                }
                EnumPageType.OVERVIEW -> {
                    val localSession = Session.instance!! // TODO: exception handling
                    // When already logged in
                    child(OverviewPage::class) {
                        attrs {
                            session = localSession
                            onItemOpen = {
                                setState {
                                    page = EnumPageType.EDIT_DOCUMENT
                                }
                            }
                        }
                    }
                }
                EnumPageType.EDIT_DOCUMENT -> {
                    val localSession = Session.instance!! // TODO: exception handling
                    child(DocumentEditPage::class) {
                        attrs {
                            session = localSession
                        }
                    }
                }
            }

            loadingOverlay(state.isLoading)
        }
    }

    /**
     * Called when user had entered the username and password.
     */
    private fun onLogin(credentials: Credentials, isRegistration: Boolean) {
        GlobalScope.launch {
            try {
                setState {
                    isLoading = true
                }
                val newSession: Session
                withTimeout(5_000) { // Timeout after 5 seconds
                    delay(2000)
                    newSession = if (isRegistration) {
                        Session.register(
                            credentials.fullName,
                            credentials.email,
                            credentials.password
                        )
                    } else {
                        Session.login(credentials.email, credentials.password)
                    }
                }
                setState {
                    Session.instance = newSession // Equivalent to isLoggedIn = true
                    isLoading = false
                    page = EnumPageType.OVERVIEW
                    snackbar =
                        ViewSnackbar.SnackbarInfo("Successfully logged in!", MAlertSeverity.success)
                }
                GlobalScope.launch {
                    try {
                        while (isActive) {
                            val client = Session.instance ?: break
                            check(client.isConnected) { "Disconnect from server" }
                            delay(1000)
                        }
                    } catch (t: Throwable) {
                        if (Session.instance != null) {
                            setState {
                                Session.instance = null
                                page = EnumPageType.UNAVAILABLE
                            }
                            showSnackbar(t.message, MAlertSeverity.warning)
                        }
                    }
                }
            } catch (ex: Throwable) {
                val failMessage = "Login failed: " + ex.message
                println(failMessage)
                showSnackbar(failMessage, MAlertSeverity.error)
                setState {
                    isLoading = false
                }
            }
        }
    }

    /**
     * Disconnect client, forget secret keys and show login page.
     */
    private fun onLogout() {
        GlobalScope.launch {
            try {
                setState {
                    isLoading = true
                }
                Session.instance?.logout()
                setState {
                    Session.instance = null
                    page = EnumPageType.AUTHENTICATION
                }
                showSnackbar("Logged out", MAlertSeverity.success)
                delay(400)
            } finally {
                setState {
                    Session.instance = null
                    page = EnumPageType.AUTHENTICATION
                    isLoading = false
                }
            }
        }
    }

    /**
     * Handle reconnect to backend API.
     */
    private fun onReconnect() {
        setState {
            reconnect()
        }
    }

    /**
     * Show snackbar for 4 seconds.
     */
    private fun showSnackbar(message: String?, severity: MAlertSeverity) {
        if (message == null) {
            return
        }
        setState {
            snackbar = ViewSnackbar.SnackbarInfo(message, severity)
        }
    }

}
