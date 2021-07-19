package de.hsaalen.cmt.rest

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
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

    this@routing.routeAuthentication()
    this@routing.routeReferences()
    this@routing.routeLabels()
    this@routing.routeMetrics()
    this@routing.routeWebSockets()
}

/**
 * Generate JWT payload based on [ServerUserInfoDto].
 */
fun ServerUserInfoDto.toJwtPayload() = JwtPayload(fullName, email)
