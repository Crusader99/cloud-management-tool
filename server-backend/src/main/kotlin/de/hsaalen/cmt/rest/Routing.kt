package de.hsaalen.cmt.rest

import de.hsaalen.cmt.jwt.JwtPayload
import de.hsaalen.cmt.jwt.readJwtCookie
import de.hsaalen.cmt.jwt.updateJwtCookie
import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.dto.PacketListDto
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.websocket.handleWebSocket
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

// TODO: Replace with actual SQL table
private val registeredUsers = mutableMapOf("simon@test.de" to "12345678")

/**
 * Register and handle REST API routes from clients.
 */
fun Application.registerRoutes() = routing {
    route("/" + RestPaths.base) {
        get("/") {
            call.respondText("Hello world from backend! :-)")
        }
        post("/login") {
            val request: ClientLoginDto = call.receive()
            println("Login: " + request.email)
            if (registeredUsers[request.email] != request.passwordHashed) {
                throw SecurityException("Wrong password!")
            }
            val payload = JwtPayload("Unknown Name", request.email)
            call.response.updateJwtCookie(payload)
            call.respond(ServerUserInfoDto(payload.fullName, payload.email))
        }
        post("/register") {
            val request: ClientRegisterDto = call.receive()
            if ("@" !in request.email || "." !in request.email) {
                throw SecurityException("Invalid email!")
            } else if (request.passwordHashed.length < 8) {
                throw SecurityException("Password to short! Minimum 8 characters required")
            }
            println("Register: " + request.email)
            registeredUsers[request.email] = request.passwordHashed
            val payload = JwtPayload("Unknown Name", request.email)
            call.response.updateJwtCookie(payload)
            call.respond(ServerUserInfoDto(payload.fullName, payload.email))
        }
        authenticate {
            get("/restore") { // Check is authorization cookie is set and refresh jwt token when already logged in
                val payload = call.request.readJwtCookie()
                call.response.updateJwtCookie(payload)
                call.respond(ServerUserInfoDto(payload.fullName, payload.email))
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
            handleWebSocket()
        }
    }
    get("/metrics") { // Provide metrics for prometheus and grafana
        // More details on https://ktor.io/docs/micrometer-metrics.html#prometheus_endpoint
        call.respond(RestServer.micrometerRegistry.scrape())
    }
}
