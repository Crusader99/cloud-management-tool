package de.hsaalen.cmt.services

import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.sql.schema.UserDao
import de.hsaalen.cmt.sql.schema.UserTable
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
            UserDao.find { UserTable.email eq email }.single()
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
        if ("@" !in email || "." !in email) {
            throw SecurityException("Invalid email!")
        } else if (passwordPlain.length < 8) {
            throw SecurityException("Password to short! Minimum 8 characters required")
        }
        val passwordHashed = hashPassword(passwordPlain)
        return newSuspendedTransaction {
            try {
                val now = DateTime.now()
                UserDao.new {
                    this.fullName = fullName
                    this.email = email
                    this.dateFirstLogin = now
                    this.dateLastLogin = now
                    this.totalLogins = 1
                    this.passwordHashed = passwordHashed
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
