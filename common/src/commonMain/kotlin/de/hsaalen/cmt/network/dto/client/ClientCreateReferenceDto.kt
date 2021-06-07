package de.hsaalen.cmt.network.dto.client

import kotlinx.serialization.Serializable

/**
 * Data transfer object to describe a detailed search query to request related references.
 */
@Serializable
data class ClientCreateReferenceDto(
    val displayName: String,
    val comment: String = "",
    val content: String = ""
) : ClientDto
