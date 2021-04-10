package de.hsaalen.cmt

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.slf4j.event.Level

fun main() {
    println("Server backend started :-)")
    embeddedServer(CIO, port = 8080) {
        install(CallLogging) {
            // Configure default logging level
            level = Level.INFO
        }
        routing {
            get("/") {
                call.respondText("Hello world from backend! :-)")
            }
        }
    }.start(wait = true)
}
