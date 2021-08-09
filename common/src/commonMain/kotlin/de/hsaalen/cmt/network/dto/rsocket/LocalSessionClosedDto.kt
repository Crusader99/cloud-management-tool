package de.hsaalen.cmt.network.dto.rsocket

import kotlinx.serialization.Serializable

/**
 * This is an [LiveDto] that is transmitted by the server before closing the connection. It informs the client that
 * the session is closed by a logout performed in another browser tab of the same session.
 */
@Serializable
object LocalSessionClosedDto : LiveDto() {

    /**
     * Encrypt the data of this [LiveDto]. Note that there is no data in this packet so there is nothing to encrypt.
     */
    override fun encrypt() = this

    /**
     * Decrypt the data of this [LiveDto]. Note that there is no data in this packet so there is nothing to decrypt.
     */
    override fun decrypt() = this

}
