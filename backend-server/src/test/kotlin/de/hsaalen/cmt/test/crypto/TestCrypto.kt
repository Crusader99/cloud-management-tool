package de.hsaalen.cmt.test.crypto

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
    fun testEncryption2() {
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

}
