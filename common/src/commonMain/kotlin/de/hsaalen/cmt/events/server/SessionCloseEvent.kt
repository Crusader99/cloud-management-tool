package de.hsaalen.cmt.events.server

import de.hsaalen.cmt.events.Event

/**
 * Event to allow disconnecting all web-sockets from a session. Called when user performs logout.
 */
data class SessionCloseEvent(
    val senderEmail: String,
    val jwtToken: String,
) : Event
