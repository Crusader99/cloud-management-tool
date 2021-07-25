package de.hsaalen.cmt.rest.routes

import de.hsaalen.cmt.crypto.fromBase64
import de.hsaalen.cmt.network.*
import de.hsaalen.cmt.network.dto.client.ClientLoginDto
import de.hsaalen.cmt.network.dto.client.ClientRegisterDto
import de.hsaalen.cmt.repository.AuthenticationRepository
import de.hsaalen.cmt.rsocket.WebSocketManager
import de.hsaalen.cmt.session.getWithSession
import de.hsaalen.cmt.session.jwt.JwtCookie
import de.hsaalen.cmt.session.jwt.generateJwtToken
import de.hsaalen.cmt.session.jwt.readJwtCookie
import de.hsaalen.cmt.session.jwt.updateJwtCookie
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
    post(apiPathAuthLogout) {
        // Disconnect currently connected web sockets
        val jwtToken = call.request.cookies[JwtCookie.cookieName]
        if (jwtToken != null) {
            try {
                JwtCookie.verifyToken(jwtToken) // Throws exception when JWT token invalid
                WebSocketManager.disconnect(jwtToken) // Disconnect all web-sockets from this session
            } catch (ex: Exception) {
                logger.warn("Unable to disconnect websockets related to session", ex)
            }
        }

        // Reset cookie using http header
        call.response.cookies.appendExpired(name = JwtCookie.cookieName, path = "/", domain = "")
        call.respond(Unit)
    }
    // Check authorization cookie is set and refresh JWT token when logged in
    getWithSession(apiPathAuthRestore) {
        val payload = call.request.readJwtCookie()
        val user = repo.restore(payload.email)
        call.response.updateJwtCookie(user.generateJwtToken())
        call.respond(user)
    }
}
