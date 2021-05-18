package de.hsaalen.cmt.network.dto.server

import kotlinx.serialization.Serializable

/**
 * Data transfer object from server for the result of client's login request.
 */
@Serializable
data class ServerUserInfoDto(
    val fullName: String,
    val email: String
)