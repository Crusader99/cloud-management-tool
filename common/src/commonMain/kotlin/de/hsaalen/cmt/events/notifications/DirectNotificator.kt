package de.hsaalen.cmt.events.notifications

import de.hsaalen.cmt.events.Event
import de.hsaalen.cmt.events.GlobalEventDispatcher

/**
 * A [Notificator] notifies the registered event handlers when an event was called. Custom [Notificator]'s for example
 * would allow synchronizing events over redis.
 */
open class DirectNotificator : Notificator {

    /**
     * Calling the registered event handlers without synchronizing over Redis.
     */
    override suspend fun notify(event: Event) {
        for (child in GlobalEventDispatcher.children) {
            for (listener in child.listeners) {
                try {
                    listener.invoke(event)
                } catch (t: Throwable) {
                    val eventName = listener.parentClass.simpleName
                    val handlerClass = child.caller?.simpleName ?: "unknown"
                    GlobalEventDispatcher.logger.error(t) { "Unexpected behaviour in handler '$handlerClass' for event '$eventName'" }
                }
            }
        }
    }

}
