import de.hsaalen.cmt.crypto.generateCryptoKey
import de.hsaalen.cmt.crypto.toBase64
import de.hsaalen.cmt.network.dto.server.ServerUserInfoDto
import de.hsaalen.cmt.network.keys.PersonalKeyManagement
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNull

/**
 * Tests to ensure the personal crypto key is stored correctly in session storage.
 */
class TestCryptoKey {

    /**
     * Ensure key can be stored, loaded and deleted from session storage.
     */
    @Test
    fun testKeyManagement() {
        val key = generateCryptoKey()
        val userInfo = ServerUserInfoDto("Simon", "simon@test.de", key.toBase64())
        assertNull(PersonalKeyManagement.load(userInfo.email), "No personal key stored yet")

        assertContentEquals(key, PersonalKeyManagement.store(userInfo))
        assertContentEquals(key, PersonalKeyManagement.load(userInfo.email))

        PersonalKeyManagement.delete()
        assertNull(PersonalKeyManagement.load(userInfo.email), "No personal key already deleted")
    }

}
