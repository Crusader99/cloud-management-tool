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
        val messages = mutableListOf<String>()
        messages += "Default API-Endpoint: " + BackendLocator.defaultBackend
        if (BackendLocator.defaultBackend != BackendLocator.configuredBackend) {
            messages += "Current API-Endpoint: " + BackendLocator.configuredBackend
        }
        val newURL = show(
            title = "Define URL to REST API backend:",
            message = messages.joinToString("\n"),
            placeholder = "New API-Endpoint",
            button = "Switch"
        ) ?: return@launch
        println("Selected new API-Endpoint: $newURL")
        BackendLocator.configuredBackend = newURL
        window.location.reload()
    }
}

/**
 * Singleton class utils to configure backend path for REST-API
 */
object BackendLocator {
    private const val KEY_API_ENDPOINT = "API_ENDPOINT"

    /**
     * Calculate default backend and configure path for client.
     */
    fun execute() {
        val customApiEndpoint = window.sessionStorage.getItem(KEY_API_ENDPOINT)
        if (customApiEndpoint == null) {
            RestPaths.apiEndpoint = defaultBackend
            println("Using default REST API endpoint: $configuredBackend")
        } else {
            RestPaths.apiEndpoint = customApiEndpoint
            println("Using configured REST API endpoint: $configuredBackend")
        }
    }

    /**
     * Find the default path to REST API.
     */
    val defaultBackend: String
        get() {
            val frontendURL = window.location.toString()
            val frontendHost = window.location.host
            return if (frontendURL.startsWith("file://") || frontendHost == "appassets.androidplatform.net") {
                // This is the official default backend server
                // It can be changed using the "switch backend"-button
                "https://provider.ddnss.de/se-project/" + RestPaths.base
            } else {
                frontendURL.removeSuffix("/") + "/" + RestPaths.base
            }
        }

    /**
     * Set / read current configured path to REST API.
     */
    var configuredBackend: String
        get() = RestPaths.apiEndpoint
        set(value) {
            RestPaths.apiEndpoint = value
            window.sessionStorage.setItem(KEY_API_ENDPOINT, value)
        }

}
