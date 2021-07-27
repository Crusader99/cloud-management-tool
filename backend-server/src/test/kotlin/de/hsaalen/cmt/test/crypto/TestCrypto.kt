package de.hsaalen.cmt.test.crypto

import de.crusader.extensions.decodeBase64
import de.crusader.extensions.encodeBase64
import de.crusader.extensions.toHexStr
import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.crypto.encrypt
import de.hsaalen.cmt.crypto.generateCryptoKey
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test cryptographical functions for correct encryption/decryption.
 */
class TestCrypto {

    /**
     * Encrypt and decrypt text.
     */
    @Test
    fun testEncryption() {
        val key = generateCryptoKey()
        val plain = "123456"
        val encrypted = encrypt(plain.encodeToByteArray(), key)
        assertEquals(16, encrypted.size, "Invalid size of encrypted data")
        println("Encrypted content: " + encrypted.toHexStr())

        val decrypted = decrypt(encrypted, key)
        assertEquals(plain, decrypted.decodeToString())
    }

    /**
     * Ensure that when same content is encrypted multiple times, different output is generated.
     */
    @Test
    fun testSecureRandomizedPadding() {
        val key = generateCryptoKey()
        val encrypted1 = encrypt("123456".encodeToByteArray(), key)
        println("Encrypted content 1: " + encrypted1.toHexStr())

        repeat(5) {
            val encrypted2 = encrypt("123456".encodeToByteArray(), key)
            println("Encrypted content 2: " + encrypted2.toHexStr())
            if (!encrypted1.contentEquals(encrypted2)) {
                return
            }
        }

        throw SecurityException("Same plain content will return same encrypted content after 5 tries")
    }

    /**
     * Ensure exact same output is generated when encrypting with zero padding (without secure-randomized-padding).
     */
    @Test
    fun testZeroPadding() {
        val key = generateCryptoKey()
        val plain = "123456"
        val out1 = encrypt(plain.encodeToByteArray(), key, secureRandomizedPadding = false).encodeBase64()
        val out2 = encrypt(plain.encodeToByteArray(), key, secureRandomizedPadding = false).encodeBase64()
        assertEquals(out1, out2, "Without secure-randomized-padding the result should always be the same")

        assertEquals(plain, decrypt(out1.decodeBase64(), key, secureRandomizedPadding = false).decodeToString())
    }

}
