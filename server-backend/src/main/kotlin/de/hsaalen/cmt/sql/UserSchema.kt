package de.hsaalen.cmt.sql

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.jodatime.datetime
import java.util.*

/**
 * The postgresql table of the user data.
 */
object UserTable : UUIDTable() {
    val email = varchar("email", 64).uniqueIndex()
    val passwordHashed = varchar("password_hashed", 128)
    val fullName = varchar("full_name", 64)
    val dateLastLogin = datetime("date_last_login")
    val dateFirstLogin = datetime("date_first_login")
    val totalLogins = long("total_logins")
}

/**
 * A data access object for a user instance.
 */
class UserDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDao>(UserTable)

    var fullName by UserTable.fullName
    var email by UserTable.email
    var passwordHashed by UserTable.passwordHashed
    var dateLastLogin by UserTable.dateLastLogin
    var dateFirstLogin by UserTable.dateFirstLogin
    var totalLogins by UserTable.totalLogins
}
