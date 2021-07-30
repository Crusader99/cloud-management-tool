package de.hsaalen.cmt.events

import de.hsaalen.cmt.extensions.coroutines
import io.ktor.utils.io.core.*
import kotlinx.coroutines.launch

/**
 * Register a custom event handler for a specific event type. This function will ignore the event parameter.
 */
inline fun ListenerBundle.register(
    type: EventType,
    crossinline listener: suspend () -> Unit
) = register(type) { _: Event ->
    listener()
}

/**
 * Register a custom event handler for a specific event type. This function used an event parameter to pass values.
 */
inline fun <reified SpecificEvent : Event> ListenerBundle.register(
    type: EventType, crossinline listener: Listener<SpecificEvent>
) {
    val handler = EventHandler(SpecificEvent::class) {
        if (it is SpecificEvent) {
            listener.invoke(it)
        }
    }
    scopeElements += CustomEvent(type, handler)
}

/**
 * Execute a notification in an asynchronous way.
 */
fun launchNotification(type: EventType, event: Event = emptyEvent) {
    coroutines.launch {
        GlobalEventDispatcher.notify(type, event)
    }
}

/**
 * Notify all registered listeners to a specific event.
 */
suspend fun GlobalEventDispatcher.notify(type: EventType, event: Event = emptyEvent) {
    for (child in children) {
        for (listener in child.scopeElements) {
            if (listener is CustomEvent && listener.type == type) {
                try {
                    listener.handler.invoke(event)
                } catch (t: Throwable) {
                    val eventName = listener.handler.parentClass.simpleName
                    val handlerClass = child.caller?.simpleName ?: "unknown"
                    logger.error(t) { "Unexpected behaviour in custom handler '$handlerClass' for event '$eventName'" }
                }
            }
        }
    }
}

/**
 * Empty event to be used when event parameters can be ignored.
 */
private val emptyEvent
    get() = object : Event {}

/**
 * A custom event allows registering a handle to a specific value of the [EventType] enum.
 */
data class CustomEvent(
    val type: EventType,
    val handler: EventHandler
) : Closeable {
    override fun close() {}
}
