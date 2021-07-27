package de.hsaalen.cmt.events

import io.ktor.utils.io.core.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

/**
 * Provides extended functionality for registering DOM event listeners in an easy way.
 * Includes support for unregistering multiple events from a [ListenerBundle].
 */
fun ListenerBundle.register(target: EventTarget, eventType: String, eventListener: (Event) -> Unit) {
    scopeElements += object : Closeable {
        override fun close() {
            target.removeEventListener(eventType, eventListener)
        }
    }
    target.addEventListener(eventType, eventListener)
}
