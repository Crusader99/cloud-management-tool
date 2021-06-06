package de.hsaalen.cmt.network.dto.objects

import kotlinx.serialization.Serializable

/**
 * The mode of change when a text document is edited.
 */
@Serializable
enum class LineChangeMode {
    ADD,
    DELETE,
    MODIFY,
}
