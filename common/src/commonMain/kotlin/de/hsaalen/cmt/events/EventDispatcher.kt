package de.hsaalen.cmt.events

import kotlin.reflect.KClass

/**
 * This is a concrete subject of the observer pattern.
 */
class ListenerBundle(val caller: KClass<*>) {

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
     * Removes the all listeners of this bundle from the [GlobalEventDispatcher].
     */
    fun unregisterAll() {
        listeners.clear()
        GlobalEventDispatcher.children -= this
    }

}

