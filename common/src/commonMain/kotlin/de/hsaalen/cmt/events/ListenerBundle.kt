package de.hsaalen.cmt.events

import io.ktor.utils.io.core.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.reflect.KClass

/**
 * This is a concrete subject of the observer pattern.
 */
class ListenerBundle(val caller: KClass<*>?) {

    /**
     * Registered event listeners to be notified when event occurs.
     */
    val listeners = mutableListOf<EventHandler>()

    /**
     * Provides extended functionality for example registering DOM event listeners in an easy way.
     * Includes support for unregistering multiple events from a [ListenerBundle].
     */
    val scopeElements = mutableListOf<Closeable>()

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
     * Build a flow of events. The flow will suspend until a new event occurred.
     * Will automatically be closed when [ListenerBundle] is unregistered.
     */
    inline fun <reified SpecificEvent : Event> receiveEventsAsFlow(): Flow<SpecificEvent> {
        return receiveEventsAsFlow(SpecificEvent::class).filterIsInstance()
    }

    /**
     * Build a flow of events. The flow will suspend until a new event occurred.
     * Will automatically be closed when [ListenerBundle] is unregistered.
     */
    fun receiveEventsAsFlow(vararg eventTypes: KClass<out Event>): Flow<Event> {
        val ch = Channel<Event>()
        for (eventType in eventTypes) {
            listeners += EventHandler(eventType) {
                if (eventType.isInstance(it)) {
                    ch.send(it)
                }
            }
        }
        scopeElements += object : Closeable {
            override fun close() {
                ch.close()
            }
        }
        return ch.consumeAsFlow()
    }

    /**
     * Removes the all listeners of this bundle from the [GlobalEventDispatcher].
     */
    fun unregisterAll() {
        try {
            listeners.clear()
            scopeElements.forEach { it.close() }
            scopeElements.clear()
        } finally {
            GlobalEventDispatcher.children -= this
        }
    }

}

