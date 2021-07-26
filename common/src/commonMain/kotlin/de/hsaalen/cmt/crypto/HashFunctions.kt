package de.hsaalen.cmt.crypto

import com.soywiz.krypto.AES
import com.soywiz.krypto.Padding
import com.soywiz.krypto.SHA256
import com.soywiz.krypto.encoding.fromBase64
import com.soywiz.krypto.encoding.toBase64
import kotlin.random.Random

/**
 * Hash raw input string using sha256 and convert it to hex string.
 * Note that salting needs to happen before this method call.
 */
fun hashSHA256(input: String): String {
    try {
        return SHA256.digest(input.encodeToByteArray()).hex
    } catch (ex: Throwable) {
        throw IllegalStateException("Hashing failed", ex)
    }
}

/**
 * Padding is used to fill the remaining bytes.
 */
private val defaultSecurePadding = Padding.ISO10126Padding

/**
 * Encrypt input data with key using AES. The algorithm used
 * padding and every time the encryption is called on the same
 * input data, different output data will be generated. This
 * will prevent equality checks from attacker.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun encrypt(decrypted: ByteArray, key: ByteArray, secureRandomizedPadding: Boolean = true): ByteArray {
    try {
        val padding = if (secureRandomizedPadding) defaultSecurePadding else Padding.ZeroPadding
        return AES.encryptAes128Cbc(decrypted, key, padding)
    } catch (ex: Throwable) {
        throw IllegalStateException("Encryption failed", ex)
    }
}

/**
 * Decrypt a encrypted [ByteArray] by given key using AES.
 *
 * @param secureRandomizedPadding - Generates different output for same input, which is more secure. Enabled by default.
 */
fun decrypt(encrypted: ByteArray, key: ByteArray, secureRandomizedPadding: Boolean = true): ByteArray {
    try {
        val padding = if (secureRandomizedPadding) defaultSecurePadding else Padding.ZeroPadding
        return AES.decryptAes128Cbc(encrypted, key, padding)
    } catch (ex: Throwable) {
        throw IllegalStateException("Decryption failed", ex)
    }
}

/**
 * Generate a key with correct length that can be used for symmetric encryption/decryption.
 */
fun generateCryptoKey(): ByteArray = Random.nextBytes(128)

/**
 * Encode [ByteArray] to a base64 [String]
 */
fun ByteArray.toBase64() = toBase64()

/**
 * Decode base64 [String] to [ByteArray]
 */
fun String.fromBase64() = fromBase64()
