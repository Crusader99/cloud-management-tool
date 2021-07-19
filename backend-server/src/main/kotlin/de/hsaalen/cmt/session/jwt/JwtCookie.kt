package de.hsaalen.cmt.session.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.hsaalen.cmt.utils.JsonHelper
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import java.util.*

/**
 * Helper object to handle JWT authentication correctly.
 */
object JwtCookie {

    const val cookieName = "TOKEN"

    const val claimName = "payload"

    const val issuer = "Simon"

    const val maxAgeMs = 14 * 24 * 60 * 60 * 1000L // 14 days

    val algorithm: Algorithm = Algorithm.HMAC512("The secret key")

    /**
     * Generate new JWT token and use the given payload data.
     */
    fun generateToken(payload: JwtPayload): String = JWT.create()
        .withSubject(cookieName)
        .withIssuer(issuer)
        .withClaim(claimName, JsonHelper.encode(payload))
        .withExpiresAt(Date(System.currentTimeMillis() + maxAgeMs))
        .sign(algorithm)
}

/**
 * Extension method to extract the payload object from JWT credentials.
 */
fun JWTCredential.toPayload(): JwtPayload = JsonHelper.decode(payload.getClaim(JwtCookie.claimName).asString())

/**
 * Extension method to read to JWT payload from a JWT HTTP cookie.
 */
fun ApplicationRequest.readJwtCookie() = call.authentication.principal<JwtPayload>()
    ?: throw SecurityException("Unable to extract payload from JWT cookie")

/**
 * Extension method to update a HTTP cookie with new valid JWT token.
 */
fun ApplicationResponse.updateJwtCookie(payload: JwtPayload) = cookies.append(
    name = JwtCookie.cookieName, // The key name of the cookie
    value = payload.generateToken(), // JWT token
    maxAge = JwtCookie.maxAgeMs / 1000L, // Age in seconds
    domain = "",
    path = "/",
//    secure = true, // Requires https protocol
    httpOnly = true, // Access only from HTTP headers
//    extensions = mapOf("SameSite" to "None") // Not required in same site
)
