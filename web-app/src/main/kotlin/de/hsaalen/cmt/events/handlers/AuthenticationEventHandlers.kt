package de.hsaalen.cmt.events.handlers

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import de.hsaalen.cmt.EnumPageType
import de.hsaalen.cmt.events.*
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.network.session.Session
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import mu.KotlinLogging
import react.setState
import kotlin.coroutines.coroutineContext

/**
 * Global event handlers for the GUI application.
 */
object AuthenticationEventHandlers {
    /**
     * Logging instance for this class.
     */
    private val logger = KotlinLogging.logger("AuthenticationEventHandlers")

    /**
     * Initialize global authentication event handlers.
     */
    fun init() {
        GlobalEventDispatcher.createBundle(this) {
            // Clientside events
            register(EventType.PRE_RECONNECT, ::onReconnect)
            register(EventType.START_KEEP_ALIVE_JOB, ::runKeepAliveJob)
            register(EventType.PRE_LOGIN, ::onLogin)
            register(EventType.PRE_LOGOUT, ::onLogout)
        }
    }

    /**
     * Configures the React state to open the connecting page.
     */
    private suspend fun onReconnect() {
        GuiOperations.setPage(EnumPageType.CONNECTING)
        GuiOperations.loading {
            try {
                logger.info { "Try restoring session...." }
                val restoredSession = Session.restore()
                // Only print overview page when session restore was successful
                val nextPage = if (restoredSession) EnumPageType.OVERVIEW else EnumPageType.AUTHENTICATION
                GuiOperations.setPage(nextPage)
                launchNotification(EventType.START_KEEP_ALIVE_JOB)
            } catch (ex: ConnectException) {
                delay(2000)
                GuiOperations.setPage(EnumPageType.UNAVAILABLE)
            } catch (ex: Exception) {
                // Ignore other errors
                logger.error(ex) { "Unable to restore session" }
            }
        }
    }

    /**
     * Suspending function that checks working connection with backend.
     * Shows backend-unavailable page when connection broken.
     */
    private suspend fun runKeepAliveJob() {
        try {
            logger.debug { "Started keep-alive job" }
            while (coroutineContext.isActive) {
                val client = Session.instance ?: break
                check(client.isConnected) { "Disconnected from server" }
                delay(1000)
            }
            logger.debug { "Job no longer active. Logged out?" }
        } catch (t: Throwable) {
            val webApp = GuiOperations.webApp
            if (Session.instance != null && webApp.state.page.isLoggedIn) {
                webApp.setState {
                    page = EnumPageType.UNAVAILABLE
                }
                val errorMessage = t.message ?: "Unknown error occurred"
                logger.info(t) { "Backend seems to be unavailable: $errorMessage" }
                GuiOperations.showSnackBar(errorMessage, MAlertSeverity.warning)
            }
        }
    }

    /**
     * Disconnect client, forget secret keys and show login page.
     */
    private suspend fun onLogout() {
        try {
            GuiOperations.loading {
                Session.instance?.logout()
                GuiOperations.setPage(EnumPageType.AUTHENTICATION)
                coroutines.launch {
                    GuiOperations.showSnackBar("Logged out", MAlertSeverity.success)
                }
                delay(400)
            }
        } finally {
            GuiOperations.setPage(EnumPageType.AUTHENTICATION)
        }
    }

    /**
     * Called when user had entered the username and password.
     */
    private suspend fun onLogin(event: LoginEvent) {
        try {
            GuiOperations.loading {
                withTimeout(5_000) { // Timeout after 5 seconds
                    delay(2000)
                    if (event.isRegistration) {
                        Session.register(
                            event.credentials.fullName,
                            event.credentials.email,
                            event.credentials.password
                        )
                    } else {
                        Session.login(event.credentials.email, event.credentials.password)
                    }
                }
                GuiOperations.setPage(EnumPageType.OVERVIEW)
            }
            coroutines.launch {
                GuiOperations.showSnackBar("Successfully logged in!", MAlertSeverity.success)
            }
            launchNotification(EventType.START_KEEP_ALIVE_JOB)
        } catch (ex: Throwable) {
            val failMessage = "Login failed: " + ex.message
            logger.warn { failMessage }
            coroutines.launch {
                GuiOperations.showSnackBar(failMessage, MAlertSeverity.error)
            }
        }
    }

}
