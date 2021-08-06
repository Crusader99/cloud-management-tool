package de.hsaalen.cmt.events.server

import de.hsaalen.cmt.events.Event
import kotlinx.serialization.Serializable

/**
 * Event to allow disconnecting all web-sockets from a session. Called when user performs logout.
 */
@Serializable
data class SessionCloseEvent(
    val senderEmail: String,
    val jwtToken: String,
) : Event
