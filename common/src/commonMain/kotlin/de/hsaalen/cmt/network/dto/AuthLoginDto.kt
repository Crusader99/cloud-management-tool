package de.hsaalen.cmt.network.dto

import kotlinx.serialization.Serializable

/**
 * Data transfer object for login request from client.
 */
@Serializable
data class AuthLoginDto(
    val username: String,
    val passwordHashed: String
)