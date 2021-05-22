package de.hsaalen.cmt.rest

import com.auth0.jwt.JWT
import de.crusader.extensions.initialCause
import de.hsaalen.cmt.jwt.JwtCookie
import de.hsaalen.cmt.jwt.toPayload
import de.hsaalen.cmt.network.dto.server.ServerErrorDto
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.auth.*
import io.ktor.http.cio.websocket.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.serialization.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.websocket.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.serialization.json.Json
import org.slf4j.event.Level
import java.time.Duration

object RestServer {
    // Registry to provide metrics for prometheus and grafana
    val micrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    /**
     * Configure an embedded HTTP server for providing a REST API.
     */
    fun configure(port: Int) = embeddedServer(CIO, port) {
        install(CallLogging) {
            // Configure default logging level
            level = Level.INFO
        }
        install(ContentNegotiation) {
            // Configure the JSON serializer
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(WebSockets) { // Define settings for the web socket connection
            pingPeriod = Duration.ofSeconds(10)
            timeout = Duration.ofSeconds(15)
        }
        install(MicrometerMetrics) {
            // Required to provide metrics for prometheus and grafana
            // More details on https://ktor.io/docs/micrometer-metrics.html#prometheus_endpoint
            registry = micrometerRegistry
        }
        install(CORS) { // For dev server CORS is required: https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
            // Allow this api to be provided on another port than the frontend
            // Examples used from https://ktor.io/docs/cors.html
            anyHost()
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Options)
            header(HttpHeaders.XForwardedProto)
            allowCredentials = true
            allowNonSimpleContentTypes = true
            maxAgeInSeconds = Duration.ofDays(1).seconds
        }
        install(StatusPages) { // Error handling on server side
            exception<Exception> { cause ->
                // Handle unexpected errors occurred in server and send a valid json to client
                val message = cause.message ?: cause.initialCause.message ?: "Unknown error occurred"
                call.respond(HttpStatusCode.InternalServerError, ServerErrorDto(message))
            }
        }
        install(Authentication) {
            // JWT token verification and how to parse payload data
            // Examples used from https://ktor.io/docs/jwt.html
            jwt {
                realm = "test"
                verifier(
                    JWT.require(JwtCookie.algorithm)
                        .withIssuer(JwtCookie.issuer)
                        .build()
                )
                validate { it.toPayload() } // Parse payload object from JSON
                authHeader {
                    // Read JWT token from cookie and provide as authorization header
                    val token = it.request.cookies[JwtCookie.cookieName]
                    parseAuthorizationHeader("Bearer $token")
                }
            }
        }
        registerRoutes() // Handle the REST API calls
    }

}