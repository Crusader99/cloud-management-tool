package de.hsaalen.cmt.session

/**
 * A context for each currently processing session. Allows passing values like the email address without passing the
 * parameter in each function call. Very useful for the dependency injection functionality.
 */
interface SessionContext {

    /**
     * E-Mail address of the current user session.
     */
    val userMail: String

    companion object {
        /**
         * This [ThreadLocal] stores a session context to each coroutine.
         */
        val threadLocal = ThreadLocal<SessionContext>()
    }
}

/**
 * Get the current [SessionContext] related to the current processing coroutine.
 */
val currentSession: SessionContext
    get() = SessionContext.threadLocal.get() ?: error("No current session context")
