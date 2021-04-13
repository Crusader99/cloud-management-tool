package de.hsaalen.cmt

import de.crusader.objects.color.Color
import de.hsaalen.cmt.views.adapter.CanvasViewAdapter
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import react.dom.h1
import react.dom.render
import styled.css
import styled.styledDiv
import kotlinx.css.*
import react.dom.h3
import styled.*

fun main() {
    println("Hello world from web-app :-)")

    render(document.getElementById("root")) {
        h1 {
            +"Hello from frontend (React+Kotlin/JS)!"
        }
        styledDiv {
            css {
                position = Position.absolute
                top = 12.px
                right = 12.px
            }
            h3 {
                +"Test"
            }
        }
    }


    document.bgColor = Color.BLUE.argbString

    val canvas = document.createElement("canvas") as HTMLCanvasElement
    document.body?.append(canvas)
    CanvasViewAdapter(canvas)
}