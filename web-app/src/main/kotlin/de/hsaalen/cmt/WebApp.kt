package de.hsaalen.cmt

import de.hsaalen.cmt.views.adapter.CanvasViewAdapter
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

fun main() {
    println("Hello world from web-app :-)")

    val canvas = document.createElement("canvas") as HTMLCanvasElement
    document.body?.append(canvas)
    CanvasViewAdapter(canvas)
}