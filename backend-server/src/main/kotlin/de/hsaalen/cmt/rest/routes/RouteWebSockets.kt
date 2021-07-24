package de.hsaalen.cmt.rest.routes

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathRSocket
import de.hsaalen.cmt.session.jwt.JwtCookie
import de.hsaalen.cmt.websocket.Connection
import io.ktor.auth.*
import io.ktor.routing.*
import io.ktor.utils.io.core.*
import io.rsocket.kotlin.transport.ktor.server.rSocket


/**
 * Handler for web-sockets that are used for live synchronisation of cloud data, e.g. file edit.
 */
fun Route.routeWebSockets() = route("/" + RestPaths.base) {
    authenticate {
        rSocket(apiPathRSocket) {
            // Get JWT token from setup payload. Setup payload is independent from cookie
            val jwt = config.setupPayload.data.readBytes().decodeToString()

            // Validate JWT token: Throws exception when invalid
            val payload = JwtCookie.verifyToken(jwt)

            // Create handle connection
            val connection = Connection(requester, payload, jwt)
            connection.handler(this@route)
        }
    }
}
