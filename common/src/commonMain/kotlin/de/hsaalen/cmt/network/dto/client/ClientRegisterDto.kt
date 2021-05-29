package de.hsaalen.cmt.network.dto.client

import kotlinx.serialization.Serializable

/**
 * Data transfer object for account register request from client.
 */
@Serializable
data class ClientRegisterDto(
    val fullName: String,
    val email: String,
    val passwordHashed: String
) : ClientDto
