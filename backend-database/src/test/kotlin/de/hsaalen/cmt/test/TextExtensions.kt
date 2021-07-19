package de.hsaalen.cmt.test

import de.hsaalen.cmt.DatabaseModules
import de.hsaalen.cmt.repository.AuthenticationRepositoryImpl
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

/**
 * Default mail used for test units,
 */
const val defaultUserMail = "simon@test.de"

/**
 * Default logger instance for test units,
 */
val logger = KotlinLogging.logger {}

/**
 * Connect to databases created by docker in gradle test task and create default user.
 */
fun registerDefaultUser() {
    DatabaseModules.init()
    runBlocking {
        try {
            AuthenticationRepositoryImpl.register("Simon", defaultUserMail, "123456")
        } catch (ex: Exception) {
            // Ignore when user already registered
            logger.info("Could not register user. This is normal while tests, when user already created", ex)
        }
    }
}
