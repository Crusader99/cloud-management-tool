package de.hsaalen.cmt.rest

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.rest.routes.*
import de.hsaalen.cmt.session.jwt.JwtCookie
import de.hsaalen.cmt.session.jwt.JwtPayload
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
    routeLabels()
    routeMetrics()
    routeWebSockets()
}

/**
 * Generate JWT token based on [ServerUserInfoDto].
 */
fun ServerUserInfoDto.generateJwtToken(): String {
    val payload = JwtPayload(fullName, email)
    var token = jwtToken
    if (token.isBlank()) {
        // Inject new JWT token in ServerUserInfoDto
        token = JwtCookie.generateToken(payload)
        jwtToken = token
    }
    return token
}
