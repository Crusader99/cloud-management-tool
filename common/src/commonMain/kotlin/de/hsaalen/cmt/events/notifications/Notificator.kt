package de.hsaalen.cmt.events.notifications

import de.hsaalen.cmt.events.Event

/**
 * A [Notificator] notifies the registered event handlers when an event was called. Custom [Notificator]'s for example
 * would allow synchronizing events over redis.
 */
interface Notificator {

    /**
     * Calling the registered event handlers. This may add support for synchronizing with other redis instances.
     */
    suspend fun notify(event: Event)

}
