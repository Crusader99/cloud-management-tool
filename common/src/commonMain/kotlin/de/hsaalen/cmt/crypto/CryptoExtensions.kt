package de.hsaalen.cmt.crypto

import com.soywiz.krypto.encoding.fromBase64
import com.soywiz.krypto.encoding.toBase64
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.utils.prohibitLineBreaks

/**
 * Decrypt [List] of [String]'s using users personal session key.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun Iterable<String>.decrypt(secureRandomizedPadding: Boolean = true): List<String> =
    map { decrypt(it, secureRandomizedPadding).prohibitLineBreaks() }

/**
 * Decrypt [Sequence] of [String]'s using users personal session key.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun Sequence<String>.decrypt(secureRandomizedPadding: Boolean = true): Sequence<String> =
    map { decrypt(it, secureRandomizedPadding).prohibitLineBreaks() }

/**
 * Decrypt [String]'s using users personal session key.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun decrypt(encrypted: String, secureRandomizedPadding: Boolean = true): String =
    decrypt(encrypted.fromBase64(), Session.personalKey, secureRandomizedPadding).decodeToString()

/**
 * Encrypt [List] of [String]'s using users personal session key.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun Iterable<String>.encrypt(secureRandomizedPadding: Boolean = true): List<String> =
    map { encrypt(it.prohibitLineBreaks(), secureRandomizedPadding) }

/**
 * Encrypt [Sequence] of [String]'s using users personal session key.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun Sequence<String>.encrypt(secureRandomizedPadding: Boolean = true): Sequence<String> =
    map { encrypt(it.prohibitLineBreaks(), secureRandomizedPadding) }

/**
 * Encrypt [String]'s using users personal session key.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun encrypt(plainText: String, secureRandomizedPadding: Boolean = true): String =
    encrypt(plainText.encodeToByteArray(), Session.personalKey, secureRandomizedPadding).toBase64()
