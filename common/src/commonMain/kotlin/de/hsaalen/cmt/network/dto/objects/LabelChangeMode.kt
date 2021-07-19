package de.hsaalen.cmt.network.dto.objects

import kotlinx.serialization.Serializable

/**
 * Allows to determinate it change is adding or removing a label.
 */
@Serializable
enum class LabelChangeMode {
    ADD,
    DELETE,
}
