package de.hsaalen.cmt.events

import de.hsaalen.cmt.network.dto.websocket.LiveDto

/**
 * Represent any event as [LiveDto] to allow serialization and transmitting over network.
 */
typealias Event = LiveDto

/**
 * Represent a listener when any specific event as data parameter.
 */
typealias Listener<Event> = suspend (Event) -> Unit

/**
 * Generalized listener that accepts all event types as data parameter.
 */
typealias EventListener = Listener<Event>
