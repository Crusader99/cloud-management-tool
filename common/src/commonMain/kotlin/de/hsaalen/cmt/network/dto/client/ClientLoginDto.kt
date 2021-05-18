package de.hsaalen.cmt.network.dto.client

import kotlinx.serialization.Serializable

/**
 * Data transfer object for user login request from client.
 */
@Serializable
data class ClientLoginDto(
    val email: String,
    val passwordHashed: String
)
