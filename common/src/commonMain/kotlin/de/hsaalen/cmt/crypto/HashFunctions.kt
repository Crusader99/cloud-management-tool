package de.hsaalen.cmt.crypto

import com.soywiz.krypto.SHA256

/**
 * Hash salted password and convert to hex string.
 */
fun hashSHA256(input: String): String {
    return SHA256.digest(input.encodeToByteArray()).hex
}
