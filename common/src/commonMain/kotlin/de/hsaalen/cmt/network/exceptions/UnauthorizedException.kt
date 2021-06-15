package de.hsaalen.cmt.network.exceptions

/**
 * Thrown when the network client is not authorized.
 */
class UnauthorizedException(message: String, cause: Throwable) : RuntimeException(message, cause)
