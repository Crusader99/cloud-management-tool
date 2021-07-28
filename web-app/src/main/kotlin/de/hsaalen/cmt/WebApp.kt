package de.hsaalen.cmt

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import de.hsaalen.cmt.components.appBar
import de.hsaalen.cmt.components.dialogs.InputDialogComponent
import de.hsaalen.cmt.components.dialogs.renderInputDialog
import de.hsaalen.cmt.components.dialogs.show
import de.hsaalen.cmt.components.features.ViewSnackbar
import de.hsaalen.cmt.components.features.loadingOverlay
import de.hsaalen.cmt.components.features.renderSnackbar
import de.hsaalen.cmt.components.features.show
import de.hsaalen.cmt.components.login.Credentials
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.file.FileSelector
import de.hsaalen.cmt.file.readText
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.pages.AuthenticationPage
import de.hsaalen.cmt.pages.DocumentEditPage
import de.hsaalen.cmt.pages.FallbackPage
import de.hsaalen.cmt.pages.OverviewPage
import de.hsaalen.cmt.utils.SimpleNoteImportJson
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import mu.KotlinLogging
import org.w3c.dom.events.Event
import react.*
import react.dom.header
import kotlin.coroutines.coroutineContext

/**
 * React properties of the [WebApp] component.
 */
external interface WebAppState : RState {
    var page: EnumPageType
    var reference: Reference?
    var isLoading: Boolean
}

/**
 * The main app component.
 */
@JsExport
class WebApp : RComponent<RProps, WebAppState>() {

    /**
     * Logging instance for this class.
     */
    private val logger = KotlinLogging.logger {}

    /**
     * Reference to overview page, required for refreshing references.
     */
    private var refOverview = createRef<OverviewPage>()

    /**
     * Reference to create dialog for requesting user to type a specific reference name.
     */
    private val refCreateReferenceDialog = createRef<InputDialogComponent>()

    /**
     * Reference to snack bar helper class required to send notifications etc.
     */
    private val refSnackBar = createRef<ViewSnackbar>()

    /**
     * Called when this component is loaded.
     */
    override fun WebAppState.init() = reconnect()


    /**
     * Configures the react state to open the connecting page.
     */
    private fun WebAppState.reconnect() {
        isLoading = true
        page = EnumPageType.CONNECTING
        reference = null

        coroutines.launch {
            try {
                logger.info { "Try restoring session...." }
                val restoredSession = Session.restore()
                setState {
                    // Only print overview page when session restore was successful
                    page = if (restoredSession) EnumPageType.OVERVIEW else EnumPageType.AUTHENTICATION
                    isLoading = false
                }
                launch { runKeepAliveJob() }
            } catch (ex: ConnectException) {
                delay(2000)
                setState {
                    isLoading = false
                    page = EnumPageType.UNAVAILABLE
                }
            } catch (ex: Exception) {
                // Ignore other errors
                logger.error(ex) { "Unable to restore session" }
            }
        }
    }

    /**
     * Called whenever an update is required.
     */
    override fun RBuilder.render() {
        //mThemeProvider(Theme.LIGHT.toMuiTheme()) { // TODO: re enable when fixed
        renderHeader()
        renderInputDialog(refCreateReferenceDialog)
        renderSnackbar(refSnackBar)
        loadingOverlay(state.isLoading)

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
                child(AuthenticationPage::class) {
                    attrs {
                        onLogin = { credentials -> onLogin(credentials, isRegistration = false) }
                        onRegister = { credentials -> onLogin(credentials, isRegistration = true) }
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
                        onItemOpen = { _, ref ->
                            setState {
                                reference = ref
                                page = EnumPageType.EDIT_DOCUMENT
                            }
                        }
                        ref = refOverview
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
                menuItems["Create"] = { onCreateReference() }
                menuItems["Import"] = { onImportData() }
            }
        }

        appBar(isLoggedIn = state.page.isLoggedIn, onLogout = ::onLogout, drawerMenu = menuItems)
    }

    /**
     * Called when user had entered the username and password.
     */
    private fun onLogin(credentials: Credentials, isRegistration: Boolean) {
        coroutines.launch {
            try {
                setState {
                    isLoading = true
                }
                withTimeout(5_000) { // Timeout after 5 seconds
                    delay(2000)
                    if (isRegistration) {
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
                    isLoading = false
                    page = EnumPageType.OVERVIEW
                }
                launch {
                    refSnackBar.current?.show("Successfully logged in!", MAlertSeverity.success)
                }
                launch { runKeepAliveJob() }
            } catch (ex: Throwable) {
                val failMessage = "Login failed: " + ex.message
                logger.warn { failMessage }
                setState {
                    isLoading = false
                }
                coroutines.launch {
                    refSnackBar.current?.show(failMessage, MAlertSeverity.error)
                }
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
            if (Session.instance != null && state.page.isLoggedIn) {
                setState {
                    page = EnumPageType.UNAVAILABLE
                }
                val errorMessage = t.message ?: "Unknown error occurred"
                logger.info(t) { "Backend seems to be unavailable: $errorMessage" }
                refSnackBar.current?.show(errorMessage, MAlertSeverity.warning)
            }
        }
    }

    /**
     * Disconnect client, forget secret keys and show login page.
     */
    private fun onLogout() {
        coroutines.launch {
            try {
                setState {
                    isLoading = true
                }
                Session.instance?.logout()
                setState {
                    page = EnumPageType.AUTHENTICATION
                }
                launch {
                    refSnackBar.current?.show("Logged out", MAlertSeverity.success)
                }
                delay(400)
            } finally {
                setState {
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
     * Import data from simplenote json format.
     */
    private fun onImportData() {
        suspend fun importFile(name: String, content: String) {
            logger.info { "importing..." }
            val session = Session.instance!!
            when {
                name == "notes.json" -> SimpleNoteImportJson.import(content).forEach { session.createReference(it) }
                name.endsWith(".txt", true) -> session.createReferenceToDocument(name, content)
                else -> throw UnsupportedOperationException("File format unsupported: $name")
            }
        }

        coroutines.launch {
            try {
                setState {
                    isLoading = true
                }
                for (file in FileSelector.openDialog()) {
                    logger.info { "Importing " + file.name + "..." }
                    val text = file.readText()
                    importFile(file.name, text)
                    logger.info { file.name + " successfully imported" }
                }
            } finally {
                setState {
                    isLoading = false
                }
            }
        }
    }

    /**
     * Create a new reference object on server.
     */
    private fun onCreateReference() {
        coroutines.launch {
            val displayName = refCreateReferenceDialog.current?.show(
                title = "Name for new reference",
                placeholder = "Display name",
                button = "Create"
            ) ?: return@launch
            logger.info { "Selected display name: $displayName" }
            Session.instance?.createReferenceToDocument(displayName)
        }
    }

}
