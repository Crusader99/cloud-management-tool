package de.hsaalen.cmt

import kotlinx.browser.window

/**
 * Hold static settings for the hole web application.
 */
object Preferences {

    /**
     * Determinate whether the application is running in debug mode. Note that the app will use a
     * different port for the REST API server in debug mode.
     */
    var isDebugMode: Boolean

    init {
        isDebugMode = window.location.port != "80" // Default HTTP port
                && window.location.port != "443" // Default HTTPS port
    }

}