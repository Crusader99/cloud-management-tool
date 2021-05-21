package de.hsaalen.cmt.exceptions

/**
 * Thrown to indicate an exception that is caused form outside of the application e.g. by a wrong configuration file.
 */
class ConfigurationException(cause: Throwable) :
    IllegalArgumentException("Configuration seem to be wrong", cause.simplify())

/**
 * Removes a unnecessary ExceptionInInitializerError caused-by definition to make the error message more human readable.
 */
private fun Throwable.simplify(): Throwable {
    if (this is ExceptionInInitializerError) {
        return cause ?: this
    }
    return this
}
