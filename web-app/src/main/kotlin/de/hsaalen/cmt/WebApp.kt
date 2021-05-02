package de.hsaalen.cmt

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import com.ccfraser.muirwik.components.lab.alert.mAlert
import com.ccfraser.muirwik.components.mSnackbar
import de.hsaalen.cmt.network.Client
import de.hsaalen.cmt.pages.LoginPage
import de.hsaalen.cmt.pages.MainPage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import materialui.styles.themeprovider.themeProvider
import react.*


/**
 * The main app component.
 */
class WebApp : RComponent<RProps, WebApp.State>() {

    interface State : RState {
        var isLoggedIn: Boolean
        var snackbar: SnackbarInfo?
    }

    /**
     * Called when this component is loaded.
     */
    override fun State.init() {
        isLoggedIn = false
        snackbar = null
    }

    /**
     * Holds information to be displayed later in snackbar gui object.
     */
    data class SnackbarInfo(
        val message: String,
        val severity: MAlertSeverity
    )

    /**
     * Called whenever an update is required.
     */
    override fun RBuilder.render() {
        themeProvider(Themes.LIGHT) {
            val info = state.snackbar
            if (info != null) {
                mSnackbar(open = true) {
                    mAlert(message = info.message, severity = info.severity)
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
                        snackbar = SnackbarInfo("Checking...", MAlertSeverity.info)
                    }
                    delay(2000)
                    Client.login(credentials.username, credentials.password)
                }
                setState {
                    isLoggedIn = true
                    snackbar = SnackbarInfo("Successfully logged in!", MAlertSeverity.success)
                }
                delay(4000)
                resetSnackbar()
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
        GlobalScope.launch {
            setState {
                snackbar = SnackbarInfo(message, severity)
            }
            delay(4000)
            resetSnackbar()
        }
    }

    /**
     * Close snackbar.
     */
    private fun resetSnackbar() {
        setState {
            snackbar = null
        }
    }

}