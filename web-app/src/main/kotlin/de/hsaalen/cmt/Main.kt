package de.hsaalen.cmt

import de.hsaalen.cmt.extensions.BackendLocator
import de.hsaalen.cmt.extensions.ExceptionHandler
import kotlinx.browser.document
import react.dom.render

/**
 * This is the entry point for the web application.
 */
fun main() {
    // Clearer representation of the errors
    ExceptionHandler.install()

    // Configure location of REST-API backend
    BackendLocator.execute()

    // Render web app
    render(document.getElementById("root")) {
        child(WebApp::class) {}
    }
}
