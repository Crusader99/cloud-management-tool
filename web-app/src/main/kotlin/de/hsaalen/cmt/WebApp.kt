package de.hsaalen.cmt

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import de.hsaalen.cmt.components.ViewHeader
import de.hsaalen.cmt.components.features.ViewSnackbar
import de.hsaalen.cmt.components.features.loadingOverlay
import de.hsaalen.cmt.components.login.Credentials
import de.hsaalen.cmt.network.client.Session
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.pages.FallbackPage
import de.hsaalen.cmt.pages.LoginPage
import de.hsaalen.cmt.pages.MainPage
import kotlinx.coroutines.*
import materialui.styles.themeprovider.themeProvider
import react.*
import react.dom.header


/**
 * The main app component.
 */
class WebApp : RComponent<RProps, WebApp.State>() {
    interface State : RState {
        var session: Session?
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
        session = null
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
                        session = restoredSession
                        page = EnumPageType.OVERVIEW
                    }
                }
            } catch (ex: ConnectException) {
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
                }
                EnumPageType.UNAVAILABLE -> {
                    child(FallbackPage::class) {
                        attrs {
                            onRetry = {
                                setState {
                                    reconnect()
                                }
                            }
                        }
                    }
                }
                EnumPageType.AUTHENTICATION -> {
                    // Allow user to login
                    child(LoginPage::class) {
                        attrs {
                            onLogin = { credentials -> onLogin(credentials, isRegistration = false) }
                            onRegister = { credentials -> onLogin(credentials, isRegistration = true) }
                            lastEmail = ""
                            isEnabled = !state.isLoading
                        }
                    }
                }
                EnumPageType.OVERVIEW -> {
                    val localSession = state.session!! // TODO: exception handling
                    // When already logged in
                    child(MainPage::class) {
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
                        Session.register(credentials.fullName, credentials.email, credentials.password)
                    } else {
                        Session.login(credentials.email, credentials.password)
                    }
                }
                setState {
                    session = newSession // Equivalent to isLoggedIn = true
                    isLoading = false
                    page = EnumPageType.OVERVIEW
                    snackbar = ViewSnackbar.SnackbarInfo("Successfully logged in!", MAlertSeverity.success)
                }
                GlobalScope.launch {
                    try {
                        while (isActive) {
                            val client = state.session ?: break
                            check(client.isConnected) { "Disconnect from server" }
                            delay(1000)
                        }
                    } catch (t: Throwable) {
                        if (state.session != null) {
                            onLogout(t.message)
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

    private fun onLogout() = onLogout(null)

    /**
     * Disconnect client, forget secret keys and show login page.
     */
    private fun onLogout(reason: String?) {
        GlobalScope.launch {
            try {
                setState {
                    isLoading = true
                }
                state.session?.logout()
                setState {
                    session = null
                    page = EnumPageType.AUTHENTICATION
                }
                var message = "Logged out"
                var severity = MAlertSeverity.success
                if (reason != null) {
                    message += ": "
                    message += reason
                    severity = MAlertSeverity.warning
                }
                showSnackbar(message, severity)
                delay(400)
            } finally {
                setState {
                    session = null
                    page = EnumPageType.AUTHENTICATION
                    isLoading = false
                }
            }
        }
    }

    /**
     * Show snackbar for 4 seconds.
     */
    private fun showSnackbar(message: String, severity: MAlertSeverity) {
        setState {
            snackbar = ViewSnackbar.SnackbarInfo(message, severity)
        }
    }

}
