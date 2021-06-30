package de.hsaalen.cmt.repositories

import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.repository.AuthenticationRepository
import de.hsaalen.cmt.sql.schema.UserDao
import de.hsaalen.cmt.sql.schema.UserTable
import de.hsaalen.cmt.utils.validateEmailAndThrow
import de.hsaalen.cmt.utils.validatePasswordAndThrow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.joda.time.DateTime

/**
 * Repository layer for providing user authentication functionality.
 */
object RepositoryUsers : AuthenticationRepository {

    /**
     * Handles register request and provides a ServerUserInfoDto when successfully logged in or throws an exception when
     * operation fails.
     */
    override suspend fun register(fullName: String, email: String, passwordPlain: String): ServerUserInfoDto {
        try {
            // Check user input and throw exception when invalid.
            email.validateEmailAndThrow()
            passwordPlain.validatePasswordAndThrow()
        } catch (ex: Exception) {
            throw SecurityException(ex.message, ex)
        }

        // Check if user email already registered in database.
        if (getUserByMail(email) != null) {
            throw IllegalArgumentException("User with email '$email' already registered")
        }

        val passwordHashed = hashPassword(passwordPlain)
        return newSuspendedTransaction {
            val now = DateTime.now()
            UserDao.new {
                this.email = email
                this.passwordHashed = passwordHashed
                this.fullName = fullName
                this.dateLastLogin = now
                this.dateFirstLogin = now
                this.datePasswordChange = now
                this.totalLogins = 1
            }.toServerUserInfoDto()
        }
    }

    /**
     * Handles login request and provides a ServerUserInfoDto when successfully logged in or throws an exception when
     * operation fails.
     */
    override suspend fun login(email: String, passwordPlain: String): ServerUserInfoDto {
        val user: UserDao = getUserByMail(email) ?: throw IllegalArgumentException("No user found with email=$email")
        val passwordHashed = hashPassword(passwordPlain)
        if (user.passwordHashed != passwordHashed) {
            throw SecurityException("Wrong password!")
        }
        return user.toServerUserInfoDto()
    }

    /**
     * Requests server to logout, e.g. delete cookie.
     */
    override suspend fun logout() {
        // Intended to do nothing on the database implementation
    }

    /**
     * Request server to restore client session. Session can only restored when JWT cookie is still valid.
     *
     * @return Session instance when email of session still registered.
     * @throws SecurityException user email seem not to be registered anymore.
     */
    override suspend fun restore(email: String) = getUserByMail(email)
        ?.toServerUserInfoDto()
        ?: throw SecurityException("User with email '$email' is not registered")

    /**
     * Search in SQL database for a specific user and return it when found.
     */
    private suspend fun getUserByMail(email: String): UserDao? = newSuspendedTransaction {
        UserDao.find(UserTable.email eq email).singleOrNull()
    }

    /**
     * Salt and hash the given password parameter.
     */
    private fun hashPassword(password: String): String {
        return password // TODO: implement
    }

}
