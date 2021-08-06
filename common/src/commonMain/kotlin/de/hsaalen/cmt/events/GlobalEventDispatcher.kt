package de.hsaalen.cmt.events

import de.hsaalen.cmt.events.notifications.DirectNotificator
import de.hsaalen.cmt.events.notifications.Notificator
import mu.KotlinLogging
import kotlin.reflect.KClass

/**
 * System-wide event dispatcher that allows dispatching events and registering event handlers through [ListenerBundle]s.
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
     * A [Notificator] notifies the registered event handlers when an event was called. Custom [Notificator]'s for
     * example would allow synchronizing events over redis.
     */
    var notificator: Notificator = DirectNotificator()

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
        notificator.notify(event)
    }
}
