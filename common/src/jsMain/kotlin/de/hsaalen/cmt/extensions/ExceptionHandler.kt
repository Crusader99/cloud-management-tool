package de.hsaalen.cmt.extensions

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineExceptionHandler
import mu.KotlinLogging
import org.w3c.dom.ErrorEvent
import org.w3c.dom.events.Event

/**
 * Custom [ExceptionHandler] to provide clearer representation of the errors.
 */
object ExceptionHandler {

    /**
     * Local logging instance.
     */
    private val logger = KotlinLogging.logger { }

    /**
     * Handler for exceptions that occurred in coroutine context.
     */
    val handler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    /**
     * Install global error handler.
     */
    fun install() {
        window.addEventListener("error", ExceptionHandler::onError)
    }

    /**
     * Called every time any unhandled exception occurred.
     */
    private fun onError(event: Event) {
        if (event !is ErrorEvent) {
            return
        }
        val error = event.error
        if (error !is Throwable) {
            return
        }
        event.preventDefault()
        onError(error)
    }

    /**
     * Called every time any unhandled exception occurred.
     */
    private fun onError(error: Throwable) {
        val errorType = error::class.simpleName?.removeSuffix("Exception") ?: ""
        val errorMessage = ("$errorType: " + error.message).trim()
        if (error.cause == null) {
            logger.warn { errorMessage }
        } else {
            logger.warn(error.cause) { errorMessage }
        }
    }

}
