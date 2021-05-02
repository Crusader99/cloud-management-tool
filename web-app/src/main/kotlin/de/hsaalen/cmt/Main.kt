package de.hsaalen.cmt

import de.hsaalen.cmt.network.Client
import de.hsaalen.cmt.network.RestPaths
import kotlinx.browser.document
import react.dom.render

/**
 * This is the entry point for the web application.
 */
fun main() {
    println("Hello world from web-app :-)")

    // Override port to REST-API server when in debug mode and without reverse proxy
    if (Preferences.isDebugMode) {
        println("This app is running in debug mode!")
        Client.apiEndpoint = "http://localhost:8080/" + RestPaths.base
        println("REST-API endpoint: " + Client.apiEndpoint)
    }

    // Render web app
    render(document.getElementById("root")) {
        child(WebApp::class) {}
    }
}