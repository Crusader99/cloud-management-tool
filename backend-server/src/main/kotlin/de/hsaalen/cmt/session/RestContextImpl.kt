package de.hsaalen.cmt.session

import de.hsaalen.cmt.session.jwt.readJwtCookie
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.withContext

/**
 * Implementation of the [SessionContext] for handling REST-API requests.
 */
class RestContextImpl(
    val call: ApplicationCall
) : SessionContext {

    /**
     * The e-mail of the current user.
     */
    override val userMail: String by lazy {
        call.request.readJwtCookie().email
    }

}

/**
 * Get the current session call from ktor library.
 */
val SessionContext.call
    get() = (this as? RestContextImpl)?.call ?: error("Not in a REST session!")

/**
 * Create session will be created and stored in a thread local attribute value to be accessed from every context.
 */
suspend inline fun RestContext.createSession(crossinline block: suspend () -> Unit) {
    val context = RestContextImpl(call)
    withContext(coroutineContext + SessionContext.threadLocal.asContextElement(context)) {
        block()
    }
}

/**
 * Handle GET requests on the given [path] parameter. Also ensures that a valid JWT token is provided by
 * the client. A thread local session will be created related to the current user.
 */
@ContextDsl
inline fun Route.getWithSession(path: String, crossinline body: RestBody) = authenticate {
    get(path) {
        createSession {
            body()
        }
    }
}

/**
 * Handle POST requests on the given [path] parameter. Also ensures that a valid JWT token is provided by
 * the client. A thread local session will be created related to the current user.
 */
@ContextDsl
inline fun Route.postWithSession(path: String, crossinline body: RestBody) = authenticate {
    post(path) {
        createSession {
            body()
        }
    }
}

/**
 * The base REST context in which a function call is applied.
 */
typealias RestContext = PipelineContext<Unit, ApplicationCall>

/**
 * Body for a POST/GET request over ktor.
 */
typealias RestBody = suspend RestContext.() -> Unit
