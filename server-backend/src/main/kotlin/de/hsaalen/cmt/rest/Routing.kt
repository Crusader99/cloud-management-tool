package de.hsaalen.cmt.rest

import de.hsaalen.cmt.jwt.JwtCookie
import de.hsaalen.cmt.jwt.JwtPayload
import de.hsaalen.cmt.jwt.readJwtCookie
import de.hsaalen.cmt.jwt.updateJwtCookie
import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.network.dto.server.ServerPreviewItemsDto
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.sql.UserDao
import de.hsaalen.cmt.sql.UserTable
import de.hsaalen.cmt.websocket.handleWebSocket
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.joda.time.DateTime

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
            val user: UserDao = newSuspendedTransaction {
                UserDao.find { UserTable.email eq request.email }.single()
            }
            if (user.passwordHashed != request.passwordHashed) {
                throw SecurityException("Wrong password!")
            }
            val payload = JwtPayload(user.fullName, request.email)
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
            newSuspendedTransaction {
                val now = DateTime.now()
                UserDao.new {
                    fullName = request.fullName
                    email = request.email
                    dateFirstLogin = now
                    dateLastLogin = now
                    totalLogins = 1
                    passwordHashed = request.passwordHashed
                }
            }
            val payload = JwtPayload(request.fullName, request.email)
            call.response.updateJwtCookie(payload)
            call.respond(ServerUserInfoDto(payload.fullName, payload.email))
        }
        post("/logout") {
            // Reset cookie using http header
            call.response.cookies.appendExpired(name = JwtCookie.cookieName, path = "/", domain = "")
            call.respond(Unit)
        }
        authenticate {
            get("/restore") { // Check is authorization cookie is set and refresh jwt token when already logged in
                val payload = call.request.readJwtCookie()
                call.response.updateJwtCookie(payload)
                call.respond(ServerUserInfoDto(payload.fullName, payload.email))
            }
            get("/list") {
                call.respond(ServerPreviewItemsDto(listOf("abc")))
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
