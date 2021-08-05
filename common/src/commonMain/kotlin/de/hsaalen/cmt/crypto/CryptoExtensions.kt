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
    decrypt(encrypted.fromBase64(), secureRandomizedPadding).decodeToString()

/**
 * Decrypt [ByteArray]'s using users personal session key.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun decrypt(encryptedData: ByteArray, secureRandomizedPadding: Boolean = true): ByteArray =
    decrypt(encryptedData, Session.personalKey, secureRandomizedPadding)

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
    encrypt(plainText.encodeToByteArray(), secureRandomizedPadding).toBase64()

/**
 * Encrypt [ByteArray]'s using users personal session key.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun encrypt(plainData: ByteArray, secureRandomizedPadding: Boolean = true): ByteArray =
    encrypt(plainData, Session.personalKey, secureRandomizedPadding)
