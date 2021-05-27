package de.hsaalen.cmt.rest

import de.hsaalen.cmt.jwt.JwtCookie
import de.hsaalen.cmt.jwt.JwtPayload
import de.hsaalen.cmt.jwt.readJwtCookie
import de.hsaalen.cmt.jwt.updateJwtCookie
import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import services.ServiceReferences
import services.ServiceUsers
import de.hsaalen.cmt.websocket.handleWebSocket
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Register and handle REST API routes from clients.
 */
fun Application.registerRoutes() = routing {
    route("/" + RestPaths.base) {
        get {
            call.respondText("Hello world from backend! :-)")
        }
        post("/login") {
            val request: ClientLoginDto = call.receive()
            println("Login: " + request.email)
            val user = ServiceUsers.login(request.email, request.passwordHashed)
            call.response.updateJwtCookie(user.toJwtPayload())
            call.respond(user)
        }
        post("/register") {
            val request: ClientRegisterDto = call.receive()
            println("Register: " + request.email)
            val user =
                ServiceUsers.register(request.fullName, request.email, request.passwordHashed)
            call.response.updateJwtCookie(user.toJwtPayload())
            call.respond(user)
        }
        post("/logout") {
            // Reset cookie using http header
            call.response.cookies.appendExpired(
                name = JwtCookie.cookieName,
                path = "/",
                domain = ""
            )
            call.respond(Unit)
        }
        authenticate {
            get("/restore") { // Check is authorization cookie is set and refresh jwt token when already logged in
                val payload = call.request.readJwtCookie()
                call.response.updateJwtCookie(payload)
                call.respond(payload.toServerUserInfoDto())
            }
            get("/listReferences") {
                val query = ClientReferenceQueryDto()
                val result = ServiceReferences.listReferences(query)
                call.respond(result)
            }
            post("/listReferences") {
                val query: ClientReferenceQueryDto = call.receive()
                val result = ServiceReferences.listReferences(query)
                call.respond(result)
            }
            get("/create") {
                call.respondText("Upload")
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

private fun ServerUserInfoDto.toJwtPayload() = JwtPayload(fullName, email)
