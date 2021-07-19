package de.hsaalen.cmt.utils

import de.hsaalen.cmt.network.dto.objects.UUID
import org.jetbrains.exposed.dao.id.EntityID

/**
 * Convert to java UUID type.
 */
val UUID.id: java.util.UUID
    get() = java.util.UUID.fromString(value)

/**
 * Convert to EntityID to UUID.
 */
fun EntityID<java.util.UUID>.toUUID() = UUID(toString())
