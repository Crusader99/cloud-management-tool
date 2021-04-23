package de.hsaalen.cmt

import de.hsaalen.cmt.rest.RestServer
import de.hsaalen.cmt.statistics.InfluxWrapper
import de.hsaalen.cmt.statistics.Stats
import io.ktor.http.*
import io.ktor.server.engine.*
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
    Thread.sleep(1000) // Delay to ensure logs come after logs from other docker containers

    logger.info("Measure startup time...")
    val started = System.currentTimeMillis()

    try {
        logger.info("Start scheduler for stats...")
        Stats.startReporting(configureInfluxWrapper())
    } catch (ex: Exception) {
        logger.warn("Disabled influx-db stats reporting due to failure", ex)
    }


    logger.info("Start embedded server...")
    configureEmbeddedServer().start(wait = true)
}

fun configureInfluxWrapper(): InfluxWrapper {
    val url = System.getenv("INFLUX_URL") ?: "http://localhost:8086"
    val database = System.getenv("INFLUX_DATABASE") ?: "db0"
    val user = System.getenv("INFLUX_USER") ?: "admin"
    val password = System.getenv("INFLUX_PASSWORD") ?: "admin"
    return InfluxWrapper(Url(url), database, user, password)
}

fun configureEmbeddedServer(): ApplicationEngine {
    val port = System.getenv("REST_PORT")?.toInt() ?: 8080
    return RestServer.start(port)
}