package de.hsaalen.cmt.rest.routes

import de.hsaalen.cmt.crypto.fromBase64
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.events.server.SessionCloseEvent
import de.hsaalen.cmt.network.*
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.repository.AuthenticationRepository
import de.hsaalen.cmt.session.currentSession
import de.hsaalen.cmt.session.getWithSession
import de.hsaalen.cmt.session.jwt.JwtCookie
import de.hsaalen.cmt.session.jwt.generateJwtToken
import de.hsaalen.cmt.session.jwt.updateJwtCookie
import de.hsaalen.cmt.session.postWithSession
import io.ktor.application.*
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
        call.response.updateJwtCookie(user.generateJwtToken())
        call.respond(user)
    }

    post(apiPathAuthRegister) {
        val request: ClientRegisterDto = call.receive()
        logger.info("Register new account with e-mail= " + request.email)
        val personalKey = request.personalEncryptedKey.fromBase64()
        val user = repo.register(request.fullName, request.email, request.passwordHashed, personalKey)
        call.response.updateJwtCookie(user.generateJwtToken())
        call.respond(user)
    }

    postWithSession(apiPathAuthLogout) { // Automatically checks if authorization cookie is valid
        // Disconnect currently connected web sockets
        call.request.cookies[JwtCookie.cookieName]?.let { jwtToken ->
            // Notify event handlers to allow disconnecting all web-sockets from this session
            GlobalEventDispatcher.notify(SessionCloseEvent(currentSession.userMail, jwtToken))
        }

        // Reset cookie using http header
        call.response.cookies.appendExpired(name = JwtCookie.cookieName, path = "/", domain = "")
        call.respond(Unit)
    }

    // Check authorization cookie is valid and refresh JWT token when logged in
    getWithSession(apiPathAuthRestore) {
        val user = repo.restore()
        call.response.updateJwtCookie(user.generateJwtToken())
        call.respond(user)
    }
}
