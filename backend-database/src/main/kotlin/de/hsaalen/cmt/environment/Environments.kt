package de.hsaalen.cmt.environment

import mu.KotlinLogging

/**
 * The local logging instance.
 *
 * Note: Already made some issues with the JVM. This has to be the first call in the file to ensure this logger is
 * initialised when an exception occurred.
 */
private val logger = KotlinLogging.logger { }.apply {
    info("Initialize environment variables")
}

val REST_PORT = envOrDefault("REST_PORT", 80).toInt()

val POSTGRESQL_HOST = envOrDefault("POSTGRESQL_HOST", "localhost")

val POSTGRESQL_PORT = envOrDefault("POSTGRESQL_PORT", 5432).toInt()

val POSTGRESQL_USER = envOrDefault("POSTGRESQL_USER", "admin")

val POSTGRESQL_PASSWORD = env("POSTGRESQL_PASSWORD")

val POSTGRESQL_DB = envOrDefault("POSTGRESQL_DB", "postgres")

val MONGO_HOST = envOrDefault("MONGO_HOST", "localhost")

val MONGO_PORT = envOrDefault("MONGO_PORT", 27017).toInt()

val MONGO_USER = envOrDefault("MONGO_USER", "admin")

val MONGO_PASSWORD = env("MONGO_PASSWORD")

/**
 * Reads a system environment variable or throws an exception when not available.
 */
private fun env(environmentName: String) = envOrNull(environmentName)
    ?: throw IllegalStateException("Environment variable '$environmentName' has to be provided")

/**
 * Reads a system environment variable or returns null when not available.
 */
private fun envOrNull(environmentName: String): String? {
    val value = System.getenv(environmentName)
    if (value == null) {
        logger.trace("Environment variable $environmentName is not provided")
    } else {
        logger.info("$environmentName=$value")
    }
    return value
}

/**
 * Reads a system environment variable or returns the given default value when not available.
 */
private fun envOrDefault(environmentName: String, default: Any): String {
    var value = envOrNull(environmentName)
    if (value == null) {
        value = default.toString()
        logger.info("$environmentName=$value (used default)")
    }
    return value
}
