package de.hsaalen.cmt.components.login

/**
 * Data (Credentials) which is typed in by the user.
 */
data class Credentials(var fullName: String = "", var email: String = "", var password: String = "")
