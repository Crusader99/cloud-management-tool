package de.hsaalen.cmt.rest.routes

import de.hsaalen.cmt.rest.RestServer
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * Register and handle REST API routes from prometheus client to generate statistics.
 */
fun Routing.routeMetrics() = get("/metrics") { // Provide metrics for prometheus and grafana
    // More details on https://ktor.io/docs/micrometer-metrics.html#prometheus_endpoint
    call.respond(RestServer.micrometerRegistry.scrape())
}
