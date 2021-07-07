package de.hsaalen.cmt.events

import kotlin.reflect.KClass

/**
 * The [EventHandler] instances map the event listener with their parent class together to allow
 * removing all listeners by the given parent class.
 */
class EventHandler(val parentClass: KClass<*>, val listener: EventListener) {

    /**
     * Call the listener with the given event data.
     */
    suspend fun invoke(event: Event) = listener(event)

}
