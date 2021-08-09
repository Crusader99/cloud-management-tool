package de.hsaalen.cmt.test.rest

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathDownloadDocument
import de.hsaalen.cmt.network.apiPathDownloadFile
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.repository.DocumentRepository
import de.hsaalen.cmt.repository.FileRepository
import de.hsaalen.cmt.test.networkTest
import de.hsaalen.cmt.test.passAuthenticationHeader
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockkObject
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * Tests that use the ktor REST API directly to ensure communication is working.
 */
class DownloadTest : KoinTest {

    /**
     * Test download of document contents.
     */
    @Test
    fun testDownloadDocument() = networkTest {
        val uuid = UUID("0053ee70-b7dc-4d9c-b2d9-7caceec79e40")
        val fileContent = "content-of\nthe-document"

        // Mock object (will be un-mocked by network test automatically)
        val repo: DocumentRepository by inject()
        mockkObject(repo)
        coEvery { repo.downloadDocument(uuid) } returns fileContent

        // Perform REST API request
        val call = handleRequest(HttpMethod.Get, RestPaths.apiEndpoint + "/$apiPathDownloadDocument/$uuid") {
            passAuthenticationHeader()
        }

        // Ensure provided result is correct
        assertEquals(HttpStatusCode.OK, call.response.status())
        assertEquals(fileContent, call.response.content)
    }

    /**
     * Test download of file contents.
     */
    @Test
    fun testDownloadFile() = networkTest {
        val uuid = UUID("0053ee70-b7dc-4d9c-b2d9-7caceec79e40")
        val fileContent = "content-of\nthe-file".encodeToByteArray()

        // Mock object (will be un-mocked by network test automatically)
        val repo: FileRepository by inject()
        mockkObject(repo)
        coEvery { repo.download(uuid) } returns fileContent

        // Perform REST API request
        val call = handleRequest(HttpMethod.Get, RestPaths.apiEndpoint + "/$apiPathDownloadFile/$uuid") {
            passAuthenticationHeader()
        }

        // Ensure provided result is correct
        assertEquals(HttpStatusCode.OK, call.response.status())
        assertContentEquals(fileContent, call.response.byteContent)
    }


}
