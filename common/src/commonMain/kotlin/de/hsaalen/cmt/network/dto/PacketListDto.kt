package de.hsaalen.cmt.network.dto

import kotlinx.serialization.Serializable

/**
 * Data transfer object for list elements.
 */
@Serializable
data class PacketListDto(
    val items: List<String>
)