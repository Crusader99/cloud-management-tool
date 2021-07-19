package de.hsaalen.cmt.test

import de.hsaalen.cmt.rest.module
import de.hsaalen.cmt.session.jwt.JwtCookie
import de.hsaalen.cmt.session.jwt.JwtPayload
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.unmockkAll

/**
 * Generate JWT token and pass as cookie header to authenticate in REST server.
 */
fun TestApplicationRequest.passAuthenticationHeader() {
    val payload = JwtPayload("Simon Forschner", "simon@test.de")
    val token = JwtCookie.generateToken(payload)
    val header = JwtCookie.cookieName + "=$token"
    addHeader(HttpHeaders.Cookie, header)
    addHeader(HttpHeaders.XForwardedProto, "https")
}

/**
 * Wrapper around the test application function to provide easier access.
 */
fun networkTest(test: TestApplicationEngine.() -> Unit) {
    withTestApplication({ module() }) {
        try {
            test()
        } finally {
            unmockkAll()
        }
    }
}
