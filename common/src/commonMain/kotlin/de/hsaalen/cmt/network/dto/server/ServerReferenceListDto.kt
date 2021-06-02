package de.hsaalen.cmt.network.dto.server

import de.hsaalen.cmt.network.dto.objects.Reference
import kotlinx.serialization.Serializable

/**
 * Data transfer object to provide a list of all related references.
 */
@Serializable
data class ServerReferenceListDto(
    val references: List<Reference>
) : ServerDto
