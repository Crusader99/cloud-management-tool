package de.hsaalen.cmt.events

import mu.KotlinLogging
import kotlin.reflect.KClass

/**
 * System wide event dispatcher that allows dispatching events and registering event handlers through [ListenerBundle]s.
 */
object GlobalEventDispatcher {

    /**
     * Logging instance for this class.
     */
    val logger = KotlinLogging.logger("event-dispatcher")

    /**
     * List of all currently registered child [ListenerBundle]'s.
     */
    val children = mutableListOf<ListenerBundle>()

    /**
     * Create new child bundle in which new listeners can be registered. When the listeners
     * are no longer required all listeners of a bundle can be removed at once.
     */
    fun createBundle(caller: Any, block: ListenerBundle.() -> Unit = {}) =
        createBundle(callerClass = caller::class, block)

    /**
     * Create new child bundle in which new listeners can be registered. When the listeners
     * are no longer required all listeners of a bundle can be removed at once.
     */
    fun createBundle(callerClass: KClass<*>? = null, block: ListenerBundle.() -> Unit = {}): ListenerBundle {
        val child = ListenerBundle(callerClass)
        children += child
        child.block()
        return child
    }

    /**
     * Notify registered listeners about new event data.
     */
    suspend fun notify(event: Event) {
        for (child in children) {
            for (listener in child.listeners) {
                try {
                    listener.invoke(event)
                } catch (t: Throwable) {
                    val eventName = listener.parentClass.simpleName
                    val handlerClass = child.caller?.simpleName ?: "unknown"
                    logger.error(t) { "Unexpected behaviour in handler '$handlerClass' for event '$eventName'" }
                }
            }
        }
    }
}
