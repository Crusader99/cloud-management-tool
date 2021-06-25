package de.hsaalen.cmt.rest

import de.hsaalen.cmt.jwt.JwtCookie
import de.hsaalen.cmt.jwt.JwtPayload
import de.hsaalen.cmt.jwt.readJwtCookie
import de.hsaalen.cmt.jwt.updateJwtCookie
import de.hsaalen.cmt.network.*
import de.hsaalen.cmt.network.dto.client.*
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.services.ServiceReferences
import de.hsaalen.cmt.services.ServiceUsers
import de.hsaalen.cmt.websocket.handleWebSocket
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*

/**
 * Register and handle REST API routes from clients.
 */
fun Application.registerRoutes() = routing {
    route("/" + RestPaths.base) {
        get {
            call.respondText("Hello world from backend! :-)")
        }
        post(apiPathAuthLogin) {
            val request: ClientLoginDto = call.receive()
            println("Login request with e-mail=" + request.email)
            val user = ServiceUsers.login(request.email, request.passwordHashed)
            call.response.updateJwtCookie(user.toJwtPayload())
            call.respond(user)
        }
        post(apiPathAuthRegister) {
            val request: ClientRegisterDto = call.receive()
            println("Register new account with e-mail= " + request.email)
            val user = ServiceUsers.register(request.fullName, request.email, request.passwordHashed)
            call.response.updateJwtCookie(user.toJwtPayload())
            call.respond(user)
        }
        post(apiPathAuthLogout) {
            // Reset cookie using http header
            call.response.cookies.appendExpired(
                name = JwtCookie.cookieName,
                path = "/",
                domain = ""
            )
            call.respond(Unit)
        }
        authenticate {
            get(apiPathAuthRestore) { // Check is authorization cookie is set and refresh jwt token when already logged in
                val payload = call.request.readJwtCookie()
                val email = payload.email
                if (!ServiceUsers.isRegistered(payload.email)) {
                    throw SecurityException("User with email '$email' is not registered")
                }
                call.response.updateJwtCookie(payload)
                call.respond(payload.toServerUserInfoDto())
            }
            get(apiPathListReferences) {
                val query = ClientReferenceQueryDto()
                val result = ServiceReferences.listReferences(query)
                call.respond(result)
            }
            post(apiPathListReferences) {
                val query: ClientReferenceQueryDto = call.receive()
                val result = ServiceReferences.listReferences(query)
                call.respond(result)
            }
            get(apiPathListReferences) {
                throw IllegalArgumentException("Expected POST request!")
            }
            post(apiPathCreateReference) {
                val info: ClientCreateReferenceDto = call.receive()
                val email = call.request.readJwtCookie().email
                val result = ServiceReferences.createItem(info, email)
                call.respond(result)
            }
            post(apiPathDeleteReference) {
                val info: ClientDeleteReferenceDto = call.receive()
                // TODO: check for access permissions to this file
                val result = ServiceReferences.deleteReferences(info.uuid)
                call.respond(result)
            }
            get("/upload") {
                call.respondText("Upload")
            }
            get("/download/{uuid}") {
                val uuid: String by call.parameters
                val stream = ServiceReferences.downloadContent(uuid)
                call.response.header(HttpHeaders.ContentDisposition, "attachment")
                call.respondOutputStream {
                    stream.copyTo(this)
                }
            }
            post(apiPathImport) { // TODO: update or remove
                val multipart = call.receiveMultipart()
                val email = call.request.readJwtCookie().email
                multipart.forEachPart { part ->
                    // Get all file parts of this multipart
                    if (part is PartData.FileItem) {
                        val fileName = part.originalFileName ?: "unknown"
                        val fileContent = part.streamProvider()
//                        ServiceReferences.import(fileName, fileContent, email)
                    }
                    // Close part to prevent memory leaks
                    part.dispose()
                }
                call.respondText("Imported")
            }
            handleWebSocket()
        }
    }
    get("/metrics") { // Provide metrics for prometheus and grafana
        // More details on https://ktor.io/docs/micrometer-metrics.html#prometheus_endpoint
        call.respond(RestServer.micrometerRegistry.scrape())
    }
}

/**
 * Generate JWT payload based on [ServerUserInfoDto].
 */
private fun ServerUserInfoDto.toJwtPayload() = JwtPayload(fullName, email)
