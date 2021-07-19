package de.hsaalen.cmt

import de.hsaalen.cmt.environment.REST_PORT
import de.hsaalen.cmt.events.EventHandlers
import de.hsaalen.cmt.exceptions.ConfigurationException
import de.hsaalen.cmt.rest.RestServer
import mu.KotlinLogging

/**
 * The local logging instance.
 */
private val logger = KotlinLogging.logger { }

/**
 * Main function for the backend service
 */
fun main() {
    logger.info("Starting server backend...")

    val engine = try {
        DatabaseModules.init()
        EventHandlers.init()
        RestServer.configure(REST_PORT)
    } catch (ex: Throwable) {
        throw ConfigurationException(ex)
    }

    // Start REST API server
    logger.info("Binding to port $REST_PORT...")
    engine.start(wait = true)
}
