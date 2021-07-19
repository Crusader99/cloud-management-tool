package de.hsaalen.cmt.test

import de.hsaalen.cmt.DatabaseModules
import de.hsaalen.cmt.repository.AuthenticationRepositoryImpl
import de.hsaalen.cmt.session.withWebSocketSession
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import mu.KotlinLogging

/**
 * Default mail used for test units,
 */
const val testUserMail = "simon@test.de"

/**
 * Default logger instance for test units,
 */
val logger = KotlinLogging.logger {}

/**
 * Connect to databases created by docker in gradle test task and create default user.
 */
fun registerTestUser() {
    DatabaseModules.init()
    runBlockingWithSession {
        try {
            AuthenticationRepositoryImpl.register("Simon", testUserMail, "123456")
        } catch (ex: Exception) {
            // Ignore when user already registered
            logger.info("Could not register user. This is normal while tests, when user already created", ex)
        }
    }
}

/**
 * Create a suspending function in coroutine scope with a maximum timeout
 * and a fake web-socket session to allow access to repositories.
 */
fun runBlockingWithSession(block: suspend () -> Unit) {
    runBlocking {
        withTimeout(10_000) {
            withWebSocketSession(testUserMail, "test-websocket") {
                block()
            }
        }
    }
}
