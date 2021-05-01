package de.hsaalen.cmt.rest

import de.hsaalen.cmt.network.dto.PacketListDto
import de.hsaalen.cmt.network.Paths
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Register and handle REST API routes from clients.
 */
fun Application.registerRoutes() = routing {
    route(Paths.base) {
        get("/") {
            call.respondText("Hello world from backend! :-)")
        }
        get("/list") {
            call.respond(PacketListDto(listOf("abc")))
        }
        get("/upload") {
            call.respondText("Upload")
        }
        get("/download") {
            call.respondText("Download")
        }
    }
    get("/metrics") {
        call.respond(RestServer.appMicrometerRegistry.scrape())
    }
}