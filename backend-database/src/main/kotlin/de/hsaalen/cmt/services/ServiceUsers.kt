package de.hsaalen.cmt.services

import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.sql.schema.UserDao
import de.hsaalen.cmt.sql.schema.UserTable
import de.hsaalen.cmt.utils.validateEmailAndThrow
import de.hsaalen.cmt.utils.validatePasswordAndThrow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.joda.time.DateTime

/**
 * Service layer for providing user authentication functionality.
 */
object ServiceUsers {

    /**
     * Handles login request and provides a ServerUserInfoDto when successfully logged in or throws an exception when
     * operation fails.
     */
    suspend fun login(email: String, passwordPlain: String): ServerUserInfoDto {
        val user: UserDao = newSuspendedTransaction {
            UserDao.find(UserTable.email eq email).singleOrNull()
                ?: throw IllegalArgumentException("No user found with email=$email")
        }
        val passwordHashed = hashPassword(passwordPlain)
        if (user.passwordHashed != passwordHashed) {
            throw SecurityException("Wrong password!")
        }
        return user.toServerUserInfoDto()
    }

    /**
     * Handles register request and provides a ServerUserInfoDto when successfully logged in or throws an exception when
     * operation fails.
     */
    suspend fun register(fullName: String, email: String, passwordPlain: String): ServerUserInfoDto {
        try {
            // Check user input and throw exception when invalid.
            email.validateEmailAndThrow()
            passwordPlain.validatePasswordAndThrow()
        } catch (ex: Exception) {
            throw SecurityException(ex.message, ex)
        }
        val passwordHashed = hashPassword(passwordPlain)
        return newSuspendedTransaction {
            try {
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
            } catch (ex: Exception) {
                if (UserDao.count(UserTable.email eq email) > 0) {
                    throw IllegalArgumentException("User with email '$email' already registered")
                }
                throw ex
            }
        }
    }

    /**
     * Salt and hash the given password parameter.
     */
    private fun hashPassword(password: String): String {
        return password // TODO: implement
    }

}
