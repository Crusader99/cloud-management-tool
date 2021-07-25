package de.hsaalen.cmt.test

import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.storage.StorageS3
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration test to verify the interaction with the S3 compatible
 * Minio database. This test will upload and download files.
 *
 * Note that the required databases are created create via docker before test execution.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3InfrastructureTest {

    /**
     * Connect to databases created by docker in gradle test task and create default user.
     */
    @BeforeTest
    fun setupEnvironment() = registerTestUser()

    /**
     * Tests the interaction with the S3 Minio container.
     */
    @Test
    fun testFileUploadAndDownload() {
        val content = "file-content"
        val reference = UUID("5e6703ae-9eae-46ca-ba79-fbdce6d761c2")

        StorageS3.uploadFile(reference, content.byteInputStream(), content.length.toLong())
        println("File uploaded")

        val downloaded = StorageS3.downloadFile(reference).readBytes().decodeToString()
        assertEquals(content, downloaded)
    }
}
