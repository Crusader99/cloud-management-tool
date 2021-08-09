package de.hsaalen.cmt.network.keys

import kotlinx.serialization.Serializable

/**
 * A personal key data element that can be stored in browsers local storage.
 */
@Serializable
internal class PersonalKeyData(
    val email: String,
    val cryptoKey: ByteArray,
)
