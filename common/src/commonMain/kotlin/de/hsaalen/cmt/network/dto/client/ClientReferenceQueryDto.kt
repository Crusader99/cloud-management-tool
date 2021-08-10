package de.hsaalen.cmt.network.dto.client

import kotlinx.serialization.Serializable

/**
 * Data transfer object to describe a detailed search query to request related references.
 */
@Serializable
data class ClientReferenceQueryDto(
    val searchName: String = "",
    val filterLabels: Set<String> = emptySet()
) : ClientDto
