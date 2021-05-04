package de.hsaalen.cmt

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import de.hsaalen.cmt.components.ViewHeader
import de.hsaalen.cmt.components.ViewSnackbar
import de.hsaalen.cmt.network.Client
import de.hsaalen.cmt.pages.LoginPage
import de.hsaalen.cmt.pages.MainPage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import materialui.styles.themeprovider.themeProvider
import react.*
import react.dom.header


/**
 * The main app component.
 */
class WebApp : RComponent<RProps, WebApp.State>() {

    interface State : RState {
        var isLoggedIn: Boolean
        var snackbar: ViewSnackbar.SnackbarInfo?
    }

    /**
     * Called when this component is loaded.
     */
    override fun State.init() {
        isLoggedIn = false
        snackbar = null
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
                        onLogout = {
                            setState {
                                isLoggedIn = false
                            }
                            showSnackbar("Logged out", MAlertSeverity.success)
                        }
                    }
                }
            }

            val snackbar = state.snackbar
            if (snackbar != null) {
                child(ViewSnackbar::class) {
                    attrs {
                        info = snackbar
//                        info.message = snackbar.message
//                        info.severity = snackbar.severity
                    }
                }
            }
            if (state.isLoggedIn) {
                child(MainPage::class) {}
            } else {
                child(LoginPage::class) {
                    attrs {
                        onLogin = ::onLogin
                    }
                }
            }
        }
    }

    /**
     * Called when user had entered the username and password.
     */
    private fun onLogin(credentials: LoginPage.Credentials) {
        GlobalScope.launch {
            try {
                withTimeout(5_000) { // Timeout after 5 seconds
                    println("Execute login...")
                    setState {
                        snackbar = ViewSnackbar.SnackbarInfo("Checking...", MAlertSeverity.info)
                    }
                    delay(2000)
                    Client.login(credentials.username, credentials.password)
                }
                setState {
                    isLoggedIn = true
                    snackbar =
                        ViewSnackbar.SnackbarInfo("Successfully logged in!", MAlertSeverity.success)
                }
            } catch (ex: Throwable) {
                val failMessage = "Login failed: " + ex.message
                println(failMessage)
                showSnackbar(failMessage, MAlertSeverity.error)
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