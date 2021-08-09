package de.hsaalen.cmt.network.keys

import de.hsaalen.cmt.crypto.fromBase64
import de.hsaalen.cmt.crypto.toBase64
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.utils.SerializeHelper
import kotlinx.browser.window
import org.w3c.dom.get
import org.w3c.dom.set


/**
 * User related personal private key management for storing the key in browsers session storage to allow restoring the
 * key after page reload or different user logged in.
 */
actual object PersonalKeyManagement {
    /**
     * Name of the item that is stored in browsers session storage.
     */
    private const val KEY_PERSONAL_CRYPTO_KEY = "KEY_PERSONAL_CRYPTO_KEY"

    /**
     * Currently personal private key that is related to a user account for encryption or decrypting general data
     * that is related to the user account. Usual this key is also stored in browsers session storage to allow restoring
     * the session without retyping the password.
     */
    actual val currentKey: ByteArray?
        get() = key

    /**
     * Local cache for the private key
     */
    private var key: ByteArray? = null

    /**
     * Try to load the personal key from browsers session storage by the e-mail provided as [String] parameter. Will
     * return the private personal key when found otherwise null will be returned.
     */
    actual fun load(email: String): ByteArray? {
        val keyBase64 = window.sessionStorage[KEY_PERSONAL_CRYPTO_KEY] ?: return null
        val data: PersonalKeyData = SerializeHelper.decodeProtoBuf(keyBase64.fromBase64())
        if (data.email != email) {
            return null // A private key for a different account is useless
        }
        key = data.cryptoKey
        return data.cryptoKey
    }

    /**
     * Store a private personal key, that is provided by the [ServerUserInfoDto] parameter, in browsers session storage
     * in combination with the related e-mail. The key has to be stored session storage to allow the user to restore the
     * session, e.g. after a page reload, without reentering to password to decrypt the account data.
     */
    actual fun store(unencryptedInfo: ServerUserInfoDto): ByteArray {
        val privateCryptoKey = unencryptedInfo.personalKeyBase64.fromBase64()
        val data = PersonalKeyData(unencryptedInfo.email, privateCryptoKey)
        val dataBase64 = SerializeHelper.encodeProtoBuf(data).toBase64()
        window.sessionStorage[KEY_PERSONAL_CRYPTO_KEY] = dataBase64
        key = data.cryptoKey
        return data.cryptoKey
    }

    /**
     * Remove the personal key from browsers session storage. This is usually used when user performs a logout. Will
     * prevent that other browser users save the personal key of the user. Note that even when the user has the key, the
     * user can not access the data that is related to the user account.
     */
    actual fun delete() {
        try {
            window.sessionStorage.removeItem(KEY_PERSONAL_CRYPTO_KEY)
        } finally {
            key = null // Also clear cache
        }
    }

}
