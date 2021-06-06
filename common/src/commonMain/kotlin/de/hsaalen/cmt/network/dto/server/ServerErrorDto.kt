package de.hsaalen.cmt.network.dto.server

import kotlinx.serialization.Serializable

/**
 * Data transfer object from server when any exception occurred.
 */
@Serializable
data class ServerErrorDto(
    val error: String
) : ServerDto
