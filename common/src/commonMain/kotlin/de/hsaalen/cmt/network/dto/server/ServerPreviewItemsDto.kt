package de.hsaalen.cmt.network.dto.server

import kotlinx.serialization.Serializable

/**
 * Data transfer object for list elements.
 */
@Serializable
data class ServerPreviewItemsDto(
    val items: List<String>
)
