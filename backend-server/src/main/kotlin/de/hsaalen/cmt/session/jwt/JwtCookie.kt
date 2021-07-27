package de.hsaalen.cmt.session.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import de.hsaalen.cmt.environment.DEFAULT_CREDENTIAL_VALUE
import de.hsaalen.cmt.environment.JWT_HMAC512_SECRET_KEY
import de.hsaalen.cmt.environment.JWT_ISSUER
import de.hsaalen.cmt.environment.JWT_MAX_AGE_MS
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.utils.SerializeHelper
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.request.*
import io.ktor.response.*
import mu.KotlinLogging
import java.util.*

/**
 * Helper object to handle JWT authentication correctly.
 */
object JwtCookie {

    /**
     * Key of the cookie stored in web-browser.
     */
    const val cookieName = "TOKEN"

    /**
     * Key for the claim that is used in JWT token for user payload.
     */
    const val claimName = "payload"

    /**
     * The algorithm used for generating JWT tokens.
     */
    private val algorithm: Algorithm = Algorithm.HMAC512(JWT_HMAC512_SECRET_KEY)

    /**
     * Verifier that validates JWT tokens and allows extracting payload.
     */
    val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(JWT_ISSUER).build()

    /**
     * Local logger instance for this class.
     */
    private val logger = KotlinLogging.logger { }

    init {
        if (JWT_HMAC512_SECRET_KEY == DEFAULT_CREDENTIAL_VALUE) {
            logger.warn("Please configure a secure secret key for JWT via system environment variables!")
        }
    }

    /**
     * Verify JWT token and extract [JwtPayload].
     *
     * @return JwtPayload when JWT was valid
     * @throws SecurityException when JWT was not valid
     */
    fun verifyToken(jwtToken: String): JwtPayload {
        try {
            val decodedJWT = verifier.verify(jwtToken) // Will throw exception on security issues
            val jsonClaim = decodedJWT.claims[claimName]?.asString() ?: error("Claim not found in JWT token")

            // Get user data payload from JWT token
            return SerializeHelper.decodeJson(jsonClaim)
        } catch (ex: Exception) {
            throw SecurityException("Unable to verify JWT token", ex)
        }
    }

    /**
     * Generate new JWT token and use the given payload data.
     */
    fun generateToken(payload: JwtPayload): String = JWT.create()
        .withSubject(cookieName)
        .withIssuer(JWT_ISSUER)
        .withClaim(claimName, SerializeHelper.encodeJson(payload))
        .withExpiresAt(Date(System.currentTimeMillis() + JWT_MAX_AGE_MS))
        .sign(algorithm)
}

/**
 * Extension method to extract the payload object from JWT credentials.
 */
fun JWTCredential.toPayload(): JwtPayload = SerializeHelper.decodeJson(payload.getClaim(JwtCookie.claimName).asString())

/**
 * Extension method to read to JWT payload from a JWT HTTP cookie.
 */
fun ApplicationRequest.readJwtCookie() = call.authentication.principal<JwtPayload>()
    ?: throw SecurityException("Unable to extract payload from JWT cookie")

/**
 * Extension function to generate JWT token based on [ServerUserInfoDto].
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


/**
 * Extension method to update a HTTP cookie with new valid JWT token.
 */
fun ApplicationResponse.updateJwtCookie(jwtToken: String) = cookies.append(
    name = JwtCookie.cookieName, // The key name of the cookie
    value = jwtToken, // JWT token
    maxAge = JWT_MAX_AGE_MS / 1000L, // Age in seconds
    domain = "",
    path = "/",
//    secure = true, // Requires https protocol
    httpOnly = true, // Access only from HTTP headers
//    extensions = mapOf("SameSite" to "None") // Not required in same site
)
