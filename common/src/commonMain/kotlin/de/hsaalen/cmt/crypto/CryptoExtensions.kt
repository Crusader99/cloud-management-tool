package de.hsaalen.cmt.crypto

import com.soywiz.krypto.encoding.fromBase64
import com.soywiz.krypto.encoding.toBase64
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.utils.prohibitLineBreaks

/**
 * Decrypt [List] of [String]'s using users personal session key.
 */
fun List<String>.decrypt() = map { decrypt(it).prohibitLineBreaks() }

/**
 * Decrypt [Sequence] of [String]'s using users personal session key.
 */
fun Sequence<String>.decrypt() = map { decrypt(it).prohibitLineBreaks() }

/**
 * Decrypt [String]'s using users personal session key.
 */
fun decrypt(encrypted: String): String {
    return decrypt(encrypted.fromBase64(), Session.personalKey).decodeToString()
}

/**
 * Encrypt [List] of [String]'s using users personal session key.
 */
fun List<String>.encrypt() = map { encrypt(it.prohibitLineBreaks()) }

/**
 * Encrypt [Sequence] of [String]'s using users personal session key.
 */
fun Sequence<String>.encrypt() = map { encrypt(it.prohibitLineBreaks()) }

/**
 * Encrypt [String]'s using users personal session key.
 */
fun encrypt(plainText: String): String {
    return encrypt(plainText.encodeToByteArray(), Session.personalKey).toBase64()
}
