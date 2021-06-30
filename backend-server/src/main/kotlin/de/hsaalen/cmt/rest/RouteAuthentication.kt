package de.hsaalen.cmt.rest

import de.hsaalen.cmt.jwt.JwtCookie
import de.hsaalen.cmt.jwt.readJwtCookie
import de.hsaalen.cmt.jwt.updateJwtCookie
import de.hsaalen.cmt.network.*
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.services.ServiceUsers
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Register and handle REST API routes from clients related to authentication.
 */
fun Routing.routeAuthentication() = route("/" + RestPaths.base) {
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
        get(apiPathAuthRestore) { // Check is authorization cookie is set and refresh JWT token when logged in
            val payload = call.request.readJwtCookie()
            val email = payload.email
            if (!ServiceUsers.isRegistered(payload.email)) {
                throw SecurityException("User with email '$email' is not registered")
            }
            call.response.updateJwtCookie(payload)
            call.respond(payload.toServerUserInfoDto())
        }
    }
}
