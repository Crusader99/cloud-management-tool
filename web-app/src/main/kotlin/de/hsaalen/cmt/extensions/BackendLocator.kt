package de.hsaalen.cmt.extensions

import de.hsaalen.cmt.events.EventType
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.events.GuiOperations
import de.hsaalen.cmt.events.register
import de.hsaalen.cmt.network.RestPaths
import kotlinx.browser.window
import mu.KotlinLogging

/**
 * Logging instance for this class.
 */
private val logger = KotlinLogging.logger("BackendLocator")

/**
 * Singleton class utils to configure backend path for REST-API
 */
object BackendLocator {
    private const val KEY_API_ENDPOINT = "API_ENDPOINT"

    /**
     * Calculate default backend and configure path for client.
     */
    fun init() {
        val customApiEndpoint = window.sessionStorage.getItem(KEY_API_ENDPOINT)
        if (customApiEndpoint == null) {
            RestPaths.apiEndpoint = defaultBackend
            logger.debug { "Using default REST API endpoint: $configuredBackend" }
        } else {
            RestPaths.apiEndpoint = customApiEndpoint
            logger.debug { "Using configured REST API endpoint: $configuredBackend" }
        }
        GlobalEventDispatcher.createBundle {
            register(EventType.PRE_SWITCH_BACKEND, ::handleSwitchBackendDialog)
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
                "https://provider.ddnss.de/cloud-management-tool/" + RestPaths.base
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

    /**
     * Opens a new dialog for configuring backend url for REST API in async mode.
     */
    private suspend fun handleSwitchBackendDialog() {
        val messages = mutableListOf<String>()
        messages += "Default API-Endpoint: $defaultBackend"
        if (defaultBackend != configuredBackend) {
            messages += "Current API-Endpoint: $configuredBackend"
        }
        val newURL = GuiOperations.showInputDialog(
            title = "Define URL to REST API backend:",
            message = messages.joinToString("\n"),
            placeholder = "New API-Endpoint",
            button = "Switch"
        ) ?: return
        logger.debug { "Selected new API-Endpoint: $newURL" }
        configuredBackend = newURL
        window.location.reload()
    }

}
