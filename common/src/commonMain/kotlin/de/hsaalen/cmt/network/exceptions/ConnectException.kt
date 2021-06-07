package de.hsaalen.cmt.network.exceptions

/**
 * Thrown when the network client is unable to connect to server.
 */
class ConnectException(message: String, cause: Throwable) : RuntimeException(message, cause)
