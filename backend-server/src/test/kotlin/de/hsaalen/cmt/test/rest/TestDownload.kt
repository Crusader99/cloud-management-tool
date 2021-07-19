package de.hsaalen.cmt.test.rest

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathDownload
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.repository.ReferenceRepository
import de.hsaalen.cmt.test.networkTest
import de.hsaalen.cmt.test.passAuthenticationHeader
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.mockkObject
import org.junit.jupiter.api.Test
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals

/**
 * Tests that use the ktor REST API directly to ensure communication is working.
 */
class TestDownload : KoinTest {

    /**
     * Test download of document contents.
     */
    @Test
    fun testDownload() = networkTest {
        val uuid = UUID("0053ee70-b7dc-4d9c-b2d9-7caceec79e40")
        val fileContent = "content-of\nthe-file"

        // Mock object (will be un-mocked by network test automatically)
        val repo: ReferenceRepository by inject()
        mockkObject(repo)
        coEvery { repo.downloadContent(uuid) } returns fileContent

        // Perform REST API request
        val call = handleRequest(HttpMethod.Get, RestPaths.apiEndpoint + "/$apiPathDownload/$uuid") {
            passAuthenticationHeader()
        }

        // Ensure provided result is correct
        assertEquals(HttpStatusCode.OK, call.response.status())
        assertEquals(fileContent, call.response.content)
    }


}
