package de.hsaalen.cmt.rest

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.rest.routes.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Register and handle REST API routes from clients.
 */
fun Application.registerRoutes() = routing {
    get("/" + RestPaths.base) {
        call.respondText("Hello world from backend! :-)")
    }

    routeAuthentication()
    routeReferences()
    routeFiles()
    routeLabels()
    routeMetrics()
    routeRSocket()
}
