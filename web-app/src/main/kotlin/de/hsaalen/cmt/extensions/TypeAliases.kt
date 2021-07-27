package de.hsaalen.cmt.extensions

import de.hsaalen.cmt.network.dto.objects.Reference
import org.w3c.dom.events.Event

/**
 * Type alias for an event handler related to any [Reference] related action.
 */
typealias ReferenceListener = (Event, Reference) -> Unit

/**
 * Type alias for an event handler related to an label modification.
 */
typealias LabelEditListener = (Event, Reference, String) -> Unit
