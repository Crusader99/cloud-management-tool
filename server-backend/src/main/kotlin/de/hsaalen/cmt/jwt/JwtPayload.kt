package de.hsaalen.cmt.jwt

import io.ktor.auth.*
import kotlinx.serialization.Serializable

/**
 * The JSON payload in a JWT token that is used in a HTTP header as cookie.
 */
@Serializable
data class JwtPayload(val fullName: String, val email: String) : Principal {

    /**
     * Generate new JWT token and use this payload data.
     */
    fun generateToken() = JwtCookie.generateToken(this)

}
