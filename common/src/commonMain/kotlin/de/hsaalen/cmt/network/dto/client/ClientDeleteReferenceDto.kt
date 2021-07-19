package de.hsaalen.cmt.network.dto.client

import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.serialization.Serializable

/**
 * Data transfer object to describe a detailed search query to request related references.
 */
@Serializable
data class ClientDeleteReferenceDto(
    val uuid: UUID,
) : ClientDto
