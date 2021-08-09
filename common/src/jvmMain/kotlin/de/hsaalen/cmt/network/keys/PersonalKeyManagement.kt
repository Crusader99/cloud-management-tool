package de.hsaalen.cmt.network.keys

import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto

/**
 * User related personal private key management for storing the key in browsers session storage to allow restoring the
 * key after page reload or different user logged in.
 */
actual object PersonalKeyManagement {

    /**
     * Currently personal private key that is related to a user account for encryption or decrypting general data
     * that is related to the user account. Usual this key is also stored in browsers session storage to allow restoring
     * the session without retyping the password.
     */
    actual val currentKey: ByteArray?
        get() = null

    /**
     * Try to load the personal key from browsers session storage by the e-mail provided as [String] parameter. Will
     * return the private personal key when found otherwise null will be returned.
     */
    actual fun load(email: String): ByteArray? {
        throw UnsupportedOperationException("Key management only implemented for JS web-apps")
    }

    /**
     * Store a private personal key, that is provided by the [ServerUserInfoDto] parameter, in browsers session storage
     * in combination with the related e-mail. The key has to be stored session storage to allow the user to restore the
     * session, e.g. after a page reload, without reentering to password to decrypt the account data.
     */
    actual fun store(unencryptedInfo: ServerUserInfoDto): ByteArray {
        throw UnsupportedOperationException("Key management only implemented for JS web-apps")
    }

    /**
     * Remove the personal key from browsers session storage. This is usually used when user performs a logout. Will
     * prevent that other browser users save the personal key of the user. Note that even when the user has the key, the
     * user can not access the data that is related to the user account.
     */
    actual fun delete() {
        throw UnsupportedOperationException("Key management only implemented for JS web-apps")
    }

}
