package de.hsaalen.cmt

import com.ccfraser.muirwik.components.lab.alert.MAlertSeverity
import com.ccfraser.muirwik.components.mThemeProvider
import de.hsaalen.cmt.components.appBar
import de.hsaalen.cmt.components.dialogs.DialogCreateReference
import de.hsaalen.cmt.components.dialogs.renderReferenceDialog
import de.hsaalen.cmt.components.features.ViewSnackbar
import de.hsaalen.cmt.components.features.loadingOverlay
import de.hsaalen.cmt.components.features.renderSnackbar
import de.hsaalen.cmt.components.login.Credentials
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.extensions.openFileSelector
import de.hsaalen.cmt.extensions.readText
import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.exceptions.ConnectException
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.pages.DocumentEditPage
import de.hsaalen.cmt.pages.FallbackPage
import de.hsaalen.cmt.pages.LoginPage
import de.hsaalen.cmt.pages.OverviewPage
import de.hsaalen.cmt.support.SimpleNoteImportJson
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.w3c.dom.events.Event
import react.*
import react.dom.header

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
class WebApp : RComponent<RProps, WebAppState>() {

    /**
     * Reference to overview page, required for refreshing references.
     */
    private var refOverview = createRef<OverviewPage>()

    /**
     * Reference to create dialog for requesting user to type a specific reference name.
     */
    private val refCreateReferenceDialog = createRef<DialogCreateReference>()

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
        Session.instance = null
        RestPaths.apiEndpoint = window.location.toString().removeSuffix("/") + "/" + RestPaths.base
        println("REST API endpoint: " + RestPaths.base)

        coroutines.launch {
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
        mThemeProvider(Theme.LIGHT.toMuiTheme()) {
            renderHeader()

            renderReferenceDialog(refCreateReferenceDialog)
            renderSnackbar(refSnackBar)

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
                            onItemOpen = { ref ->
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
                        }
                    }
                }
            }

            loadingOverlay(state.isLoading)
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
            }

            menuItems["Create"] = { onCreateReference() }
            menuItems["Import"] = { onImportData() }
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
                }
                launch {
                    refSnackBar.current?.show("Successfully logged in!", MAlertSeverity.success)
                }
                launch {
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
                            val errorMessage = t.message ?: "Unknown error occurred"
                            refSnackBar.current?.show(errorMessage, MAlertSeverity.warning)
                        }
                    }
                }
            } catch (ex: Throwable) {
                val failMessage = "Login failed: " + ex.message
                println(failMessage)
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
                    Session.instance = null
                    page = EnumPageType.AUTHENTICATION
                }
                coroutines.launch {
                    refSnackBar.current?.show("Logged out", MAlertSeverity.success)
                }
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
     * Import data from simplenote json format.
     */
    private fun onImportData() {
        suspend fun import(fileName: String, fileContent: String) {
            println("importing...")
            if (fileName == "notes.json") {
                for (imported in SimpleNoteImportJson.import(json = fileContent)) {
                    Session.instance!!.createReference(imported)
                }
            } else if (fileName.endsWith(".txt", true)) {
                Session.instance!!.createReference(fileName, fileContent)
            } else {
                throw UnsupportedOperationException("File format unsupported: $fileName")
            }
        }

        coroutines.launch {
            for (file in openFileSelector()) {
                // TODO: remove debug messages
                println("selected " + file.name)
                val text = file.readText()
                println("read file content")
                import(file.name, text)
                println("imported")
            }
            refOverview.current?.updateReferences()
        }
    }

    /**
     * Create a new reference object on server.
     */
    private fun onCreateReference() {
        coroutines.launch {
            val displayName = refCreateReferenceDialog.current?.show() ?: return@launch
            println("Selected display name: $displayName")
            Session.instance?.createReference(displayName)
            refOverview.current?.updateReferences()
        }
    }

}
