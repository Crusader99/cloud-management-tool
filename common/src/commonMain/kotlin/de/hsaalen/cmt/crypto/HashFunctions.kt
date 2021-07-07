package de.hsaalen.cmt.crypto

import com.soywiz.krypto.SHA256

/**
 * Hash raw input string using sha256 and convert it to hex string.
 * Note that salting needs to happen before this method call.
 */
fun hashSHA256(input: String): String {
    return SHA256.digest(input.encodeToByteArray()).hex
}
