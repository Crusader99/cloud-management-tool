package de.hsaalen.cmt.rest

import de.hsaalen.cmt.jwt.JwtCookie
import de.hsaalen.cmt.jwt.readJwtCookie
import de.hsaalen.cmt.jwt.updateJwtCookie
import de.hsaalen.cmt.network.*
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.repository.AuthenticationRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import mu.KotlinLogging
import org.koin.ktor.ext.inject

/**
 * The local logging instance for this file.
 */
private val logger = KotlinLogging.logger { }

/**
 * Register and handle REST API routes from clients related to authentication.
 */
fun Routing.routeAuthentication() = route("/" + RestPaths.base) {
    // Lazy inject AuthenticationRepository
    val repo: AuthenticationRepository by inject()

    post(apiPathAuthLogin) {
        val request: ClientLoginDto = call.receive()
        logger.info("Login request with e-mail=" + request.email)
        val user = repo.login(request.email, request.passwordHashed)
        call.response.updateJwtCookie(user.toJwtPayload())
        call.respond(user)
    }
    post(apiPathAuthRegister) {
        val request: ClientRegisterDto = call.receive()
        logger.info("Register new account with e-mail= " + request.email)
        val user = repo.register(request.fullName, request.email, request.passwordHashed)
        call.response.updateJwtCookie(user.toJwtPayload())
        call.respond(user)
    }
    post(apiPathAuthLogout) {
        // Reset cookie using http header
        call.response.cookies.appendExpired(name = JwtCookie.cookieName, path = "/", domain = "")
        call.respond(Unit)
    }
    authenticate {
        // Check is authorization cookie is set and refresh JWT token when logged in
        get(apiPathAuthRestore) {
            val payload = call.request.readJwtCookie()
            val user = repo.restore(payload.email)
            call.response.updateJwtCookie(user.toJwtPayload())
            call.respond(user)
        }
    }
}
