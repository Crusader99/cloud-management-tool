package de.hsaalen.cmt.crypto

import com.soywiz.krypto.encoding.fromBase64
import com.soywiz.krypto.encoding.toBase64
import de.hsaalen.cmt.network.session.Session

/**
 * Decrypt [List] of [String]'s using users personal session key.
 */
fun List<String>.decrypt() = map { decrypt(it) }

/**
 * Decrypt [Sequence] of [String]'s using users personal session key.
 */
fun Sequence<String>.decrypt() = map { decrypt(it) }

/**
 * Decrypt [String]'s using users personal session key.
 */
fun decrypt(encrypted: String): String {
    return decrypt(encrypted.fromBase64(), Session.personalKey).decodeToString()
}

/**
 * Encrypt [List] of [String]'s using users personal session key.
 */
fun List<String>.encrypt() = map { encrypt(it) }

/**
 * Encrypt [Sequence] of [String]'s using users personal session key.
 */
fun Sequence<String>.encrypt() = map { encrypt(it) }

/**
 * Encrypt [String]'s using users personal session key.
 */
fun encrypt(plainText: String): String {
    return encrypt(plainText.encodeToByteArray(), Session.personalKey).toBase64()
}
