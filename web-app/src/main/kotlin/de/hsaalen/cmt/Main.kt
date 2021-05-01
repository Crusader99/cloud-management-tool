package de.hsaalen.cmt

import kotlinx.browser.document
import react.dom.render

/**
 * This is the entry point for the web application.
 */
fun main() {
    println("Hello world from web-app :-)")

    render(document.getElementById("root")) {
        child(WebApp::class) {}
    }
}