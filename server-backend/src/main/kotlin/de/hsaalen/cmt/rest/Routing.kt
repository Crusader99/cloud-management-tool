package de.hsaalen.cmt.rest

import de.hsaalen.cmt.statistics.StatsBasic
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Register and handle REST API routes from clients.
 */
fun Application.registerRoutes() = routing {
    get("/") {
        call.respondText("Hello world from backend! :-)")
        StatsBasic.connects.incrementAndGet()
    }
}