package de.hsaalen.cmt.extensions

import de.hsaalen.cmt.components.dialogs.InputDialogComponent
import de.hsaalen.cmt.network.RestPaths
import kotlinx.browser.window
import kotlinx.coroutines.launch

/**
 * Opens a new dialog for configuring backend url for REST API in async mode.
 */
fun InputDialogComponent.handleSwitchBackendDialog() {
    coroutines.launch {
        val message = """
            Default API-Endpoint: ${BackendLocator.getDefaultBackend()}
            Current API-Endpoint: ${RestPaths.apiEndpoint}
        """.trimIndent()
        val newURL = show(
            title = "Define URL to REST API backend:",
            message = message,
            placeholder = "New API-Endpoint",
            button = "Switch"
        ) ?: return@launch
        println("New URL: $newURL")
        RestPaths.apiEndpoint = newURL
    }
}

/**
 * Singleton class utils to configure backend path for REST-API
 */
object BackendLocator {

    /**
     * Calculate default backend and configure path for client.
     */
    fun execute() {
        RestPaths.apiEndpoint = getDefaultBackend()
        println("REST API endpoint: " + RestPaths.apiEndpoint)
    }

    /**
     * Find the default path to REST API.
     */
    fun getDefaultBackend(): String {
        return window.location.toString().removeSuffix("/") + "/" + RestPaths.base
    }

}
