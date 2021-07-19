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

/**
 * Port to be used for REST API server.
 */
val REST_PORT = envOrDefault("REST_PORT", 80).toInt()

/**
 * Hash passwords with this salt before storing in SQL database.
 */
val PASSWORD_SALT = env("PASSWORD_SALT")

/**
 * Host address of the postgres SQL connection.
 */
val POSTGRESQL_HOST = envOrDefault("POSTGRESQL_HOST", "localhost")

/**
 * Port to the postgres SQL connection.
 */
val POSTGRESQL_PORT = envOrDefault("POSTGRESQL_PORT", 5432).toInt()

/**
 * User name for postgres SQL authorization.
 */
val POSTGRESQL_USER = envOrDefault("POSTGRESQL_USER", "admin")

/**
 * User password for postgres SQL authentication.
 */
val POSTGRESQL_PASSWORD = env("POSTGRESQL_PASSWORD")

/**
 * Database name for postgres SQL authorization.
 */
val POSTGRESQL_DB = envOrDefault("POSTGRESQL_DB", "postgres")

/**
 * Host address to the mongo database.
 */
val MONGO_HOST = envOrDefault("MONGO_HOST", "localhost")

/**
 * Port number to the mongo database.
 */
val MONGO_PORT = envOrDefault("MONGO_PORT", 27017).toInt()

/**
 * User name for mongo database authorization.
 */
val MONGO_USER = envOrDefault("MONGO_USER", "admin")

/**
 * User name for mongo database authentication.
 */
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
