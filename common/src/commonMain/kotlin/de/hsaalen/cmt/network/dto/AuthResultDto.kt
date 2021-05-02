package de.hsaalen.cmt.network.dto

import kotlinx.serialization.Serializable

/**
 * Data transfer object from server for the result of client's login request.
 */
@Serializable
data class AuthResultDto(
    var message: String,
    var success: Boolean
)