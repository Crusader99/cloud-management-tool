package de.hsaalen.cmt

import de.hsaalen.cmt.rest.RestServer
import mu.KotlinLogging

/**
 * Local logger instance
 */
private val logger = KotlinLogging.logger { }

/**
 * Main function for the backend service
 */
fun main() {
    logger.info("Starting server backend...")

    // Find configured port
    val portKey = "REST_PORT"
    logger.info("Reading $portKey environment variable...")
    val port = System.getenv(portKey)?.toInt() ?: 8080

    // Start REST API server
    logger.info("Binding to port $port...")
    val engine = RestServer.configure(port)
    engine.start(wait = true)
}