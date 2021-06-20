package de.hsaalen.cmt

import de.hsaalen.cmt.extensions.BackendLocator
import kotlinx.browser.document
import react.dom.render

/**
 * This is the entry point for the web application.
 */
fun main() {
    // Configure location of REST-API backend
    BackendLocator.execute()

    // Render web app
    render(document.getElementById("root")) {
        child(WebApp::class) {}
    }
}
