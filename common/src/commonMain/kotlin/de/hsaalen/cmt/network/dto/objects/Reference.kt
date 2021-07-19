package de.hsaalen.cmt.network.dto.objects

import kotlinx.serialization.Serializable

/**
 * A single reference object that can be serialized and transferred over network.
 */
@Serializable
data class Reference(
    val uuid: UUID,
    val accessCode: String?,
    val displayName: String,
    val contentType: String,
    val dateCreation: Long,
    val dateLastAccess: Long,
    val labels: List<String>
)
