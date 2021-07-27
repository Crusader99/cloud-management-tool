package de.hsaalen.cmt.network.dto.objects

import kotlinx.serialization.Serializable

/**
 * Content type of a reference. Different content types are handled by different databases.
 */
@Serializable
enum class ContentType {
    TEXT,
    FILE,
}
