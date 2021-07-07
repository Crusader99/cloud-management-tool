package de.hsaalen.cmt.events

import kotlin.reflect.KClass

/**
 * This is a concrete subject of the observer pattern.
 */
object GlobalEventDispatcher {

    /**
     * Registered event listeners to be notified when event occurs.
     */
    val listeners = mutableListOf<EventHandler>()

    /**
     * Add listener for handling received DTOs.
     */
    inline fun <reified SpecificEvent : Event> register(crossinline packetListener: Listener<SpecificEvent>) {
        listeners += EventHandler(SpecificEvent::class) {
            if (it is SpecificEvent) {
                packetListener(it)
            }
        }
    }

    /**
     * Remove registered event handler of a specific class.
     */
    fun unregisterAll(listenerClass: KClass<*>) {
        listeners.removeAll { it.parentClass == listenerClass }
    }

    /**
     * Notify registered listeners about new event data.
     */
    suspend fun notify(event: Event) {
        for (listener in listeners) {
            listener.invoke(event)
        }
    }

}
