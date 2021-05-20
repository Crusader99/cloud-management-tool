package de.hsaalen.cmt.network.exceptions

/**
 * Thrown when the network request to server was answered with an error status.
 */
class ServerException(val code: Int, message: String, cause: Throwable? = null) : RuntimeException(message, cause)
