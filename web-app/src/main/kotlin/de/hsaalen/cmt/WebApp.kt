package de.hsaalen.cmt

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import de.hsaalen.cmt.components.ViewHeader
import de.hsaalen.cmt.components.features.ViewSnackbar
import de.hsaalen.cmt.components.features.loadingOverlay
import de.hsaalen.cmt.components.login.Credentials
import de.hsaalen.cmt.network.Client
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
        var client: Client?
        var snackbar: ViewSnackbar.SnackbarInfo?
        var isLoading: Boolean
    }

    private val State.isLoggedIn: Boolean
        get() = client != null

    /**
     * Called when this component is loaded.
     */
    override fun State.init() {
        client = null
        snackbar = null
        isLoading = true

        GlobalScope.launch {
            val info = Client.restore()
            setState {
                isLoading = false
                if (info != null) {
                    client = Client()
                }
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
                        isLoggedIn = state.isLoggedIn
                        onLogout = ::onLogout
                    }
                }
            }

            child(ViewSnackbar::class) {
                attrs {
                    info = state.snackbar
                }
            }

            val localClient = state.client
            if (localClient != null) { // When already logged in
                child(MainPage::class) {
                    attrs {
                        client = localClient
                    }
                }
            } else { // Currently not logged in
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
                val connection: Client
                withTimeout(5_000) { // Timeout after 5 seconds
                    delay(2000)
                    connection = if (isRegistration) {
                        Client.register(credentials.fullName, credentials.email, credentials.password)
                    } else {
                        Client.login(credentials.email, credentials.password)
                    }
                }
                setState {
                    client = connection // Equivalent to isLoggedIn = true
                    isLoading = false
                    snackbar = ViewSnackbar.SnackbarInfo("Successfully logged in!", MAlertSeverity.success)
                }
                GlobalScope.launch {
                    try {
                        while (isActive) {
                            val client = state.client ?: break
                            check(client.isConnected) { "Disconnect from server" }
                            delay(1000)
                        }
                    } catch (t: Throwable) {
                        if (state.client != null) {
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
                state.client?.disconnect()
                setState {
                    client = null // Equivalent to logout
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
