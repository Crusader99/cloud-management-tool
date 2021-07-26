package de.hsaalen.cmt.utils

/**
 * Throw an exception when password is not valid.
 */
fun String.validatePasswordAndThrow() {
    val errorMessage = validatePasswordAndGetError() ?: return
    throw IllegalArgumentException(errorMessage)
}

/**
 * Return an exception message when password is not valid or null when everything okay.
 */
fun String.validatePasswordAndGetError(): String? {
    val allowedPasswordRange = 8..40
    val minLength = allowedPasswordRange.first
    if (this.length < allowedPasswordRange.first) {
        return "Password of minimum $minLength characters required"
    }
    val maxLength = allowedPasswordRange.last
    if (this.length > allowedPasswordRange.last) {
        return "Password of maximum $maxLength characters allowed"
    }
    return null
}

/**
 * Throw an exception when e-mail is not valid according to self defined rules.
 */
fun String.validateEmailAndThrow() {
    val errorMessage = validateEmailAndGetError() ?: return
    throw IllegalArgumentException(errorMessage)
}

/**
 * Return an exception message when email is not valid (according to self defined rules) or null when everything okay.
 */
fun String.validateEmailAndGetError(): String? {
    if ("@" !in this || "." !in this) {
        return "Invalid e-mail!"
    }
    return null
}

/**
 * Return an exception message when full name is not valid or null when everything okay.
 */
fun String.validateFullNameAndGetError(): String? {
    if (length < 2) {
        return "Name too short"
    } else if (length > 40) {
        return "Name too long"
    }
    return null
}

fun String.prohibitLineBreaks(): String {
    val lineNumbers = lineSequence().count()
    if (lineNumbers > 1) {
        val append = if (length < 256) ": $this" else ""
        throw IllegalStateException("Found $lineNumbers line numbers, but no line breaks allowed$append")
    }
    return this
}
