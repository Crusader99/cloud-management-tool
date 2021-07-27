package de.hsaalen.cmt.session.jwt

import io.ktor.auth.*
import kotlinx.serialization.Serializable

/**
 * The JSON payload in a JWT token that is used in a HTTP header as cookie.
 */
@Serializable
data class JwtPayload(val fullName: String, val email: String) : Principal
