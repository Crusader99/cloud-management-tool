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
 * The default value for username and password when not configured over system environment variables
 */
const val DEFAULT_CREDENTIAL_VALUE = "admin123"

/**
 * Port to be used for REST API server.
 */
val REST_PORT = envOrDefault("REST_PORT", 80).toInt()

/**
 * Name of JWT token issuer.
 */
val JWT_ISSUER = envOrDefault("JWT_ISSUER", "CloudTool")

/**
 * Password for the JWT token.
 */
val JWT_HMAC512_SECRET_KEY = envOrDefault("JWT_HMAC512_SECRET_KEY", DEFAULT_CREDENTIAL_VALUE)

/**
 * Maximum age of JWT before token expires in milliseconds. Default is 14 days.
 */
val JWT_MAX_AGE_MS = envOrDefault("JWT_MAX_AGE_MS", 14 * 24 * 60 * 60 * 1000L).toLong()

/**
 * Hash passwords with this salt before storing in SQL database.
 */
val PASSWORD_SALT = envOrDefault("PASSWORD_SALT", "salt")

/**
 * Host address of the postgres SQL connection.
 */
val POSTGRESQL_HOST = envOrDefault("POSTGRESQL_HOST", "localhost")

/**
 * Port to the postgres SQL connection.
 */
val POSTGRESQL_PORT = envOrDefault("POSTGRESQL_PORT", 5432).toInt()

/**
 * Username for postgres SQL authorization.
 */
val POSTGRESQL_USER = envOrDefault("POSTGRESQL_USER", DEFAULT_CREDENTIAL_VALUE)

/**
 * User password for postgres SQL authentication.
 */
val POSTGRESQL_PASSWORD = envOrDefault("POSTGRESQL_PASSWORD", DEFAULT_CREDENTIAL_VALUE)

/**
 * Database name for postgres SQL authorization.
 */
val POSTGRESQL_DB = envOrDefault("POSTGRESQL_DB", "postgres")

/**
 * Address of the host for connecting to the mongo database.
 */
val MONGO_HOST = envOrDefault("MONGO_HOST", "localhost")

/**
 * Port number to the mongo database.
 */
val MONGO_PORT = envOrDefault("MONGO_PORT", 27017).toInt()

/**
 * Username for mongo database authorization.
 */
val MONGO_USER = envOrDefault("MONGO_USER", DEFAULT_CREDENTIAL_VALUE)

/**
 * Password for mongo database authentication.
 */
val MONGO_PASSWORD = envOrDefault("MONGO_PASSWORD", DEFAULT_CREDENTIAL_VALUE)

/**
 * The API endpoint to be used for S3 file storage.
 */
val S3_ENDPOINT = envOrDefault("S3_ENDPOINT", "http://localhost:9000")

/**
 * The username or access key of the S3 file storage.
 */
val S3_USER = envOrDefault("S3_USER", DEFAULT_CREDENTIAL_VALUE)

/**
 * The password or secret key of the S3 file storage.
 */
val S3_PASSWORD = envOrDefault("S3_PASSWORD", DEFAULT_CREDENTIAL_VALUE)

/**
 * The name of the bucket where the files should be stored.
 */
val S3_BUCKET = envOrDefault("S3_BUCKET", "file")

/**
 * The host that is used to connect to Redis.
 */
val REDIS_HOST = envOrDefault("REDIS_HOST", "localhost")

/**
 * The port that is used to connect to Redis.
 */
val REDIS_PORT = envOrNull("REDIS_PORT")?.toIntOrNull() ?: 6379

/**
 * The topic that is used by Redis to publish events and register listeners.
 */
val REDIS_TOPIC = envOrDefault("REDIS_TOPIC", "cloud-management-tool")

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
