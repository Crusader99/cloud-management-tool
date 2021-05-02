package de.hsaalen.cmt.rest

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.metrics.micrometer.*
import io.ktor.serialization.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

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
        install(MicrometerMetrics) {
            // Required to provide metrics for prometheus and grafana
            // More details on https://ktor.io/docs/micrometer-metrics.html#prometheus_endpoint
            registry = micrometerRegistry
        }
        install(CORS) {
            anyHost() // Allow this api to be provided on another port than the frontend
        }
        registerRoutes()
    }

}