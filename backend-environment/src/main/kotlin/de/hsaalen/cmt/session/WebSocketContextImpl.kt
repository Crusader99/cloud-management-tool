package de.hsaalen.cmt.session

import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/**
 * Implementation of the [SessionContext] to provide context information to websockets.
 */
class WebSocketContextImpl(
    override val userMail: String,
    val senderSocketId: String,
) : SessionContext


/**
 * Get the current sender socket id be current [SessionContext].
 */
val SessionContext.senderSocketId: String
    get() = (this as? WebSocketContextImpl)?.senderSocketId ?: error("Not in a web-socket session!")

/**
 * Build a websocket session context for the current coroutine.
 */
suspend inline fun withWebSocketSession(userEmail: String, socketId: String, crossinline block: suspend () -> Unit) {
    val context = WebSocketContextImpl(userEmail, socketId)
    withContext(coroutineContext + SessionContext.threadLocal.asContextElement(context)) {
        block()
    }
}
