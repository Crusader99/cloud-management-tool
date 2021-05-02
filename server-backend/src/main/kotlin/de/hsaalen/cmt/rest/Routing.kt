package de.hsaalen.cmt.rest

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.dto.AuthResultDto
import de.hsaalen.cmt.network.dto.PacketListDto
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Register and handle REST API routes from clients.
 */
fun Application.registerRoutes() = routing {
    route("/" + RestPaths.base) {
        get("/") {
            call.respondText("Hello world from backend! :-)")
        }
        post("/login") {
            call.respond(AuthResultDto("Test", true))
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
    get("/metrics") { // Provide metrics for prometheus and grafana
        // More details on https://ktor.io/docs/micrometer-metrics.html#prometheus_endpoint
        call.respond(RestServer.micrometerRegistry.scrape())
    }
}