package de.hsaalen.cmt.test

import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.objects.LineChangeMode.*
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.repository.DocumentRepositoryImpl
import de.hsaalen.cmt.repository.ReferenceRepositoryImpl
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration test to verify the interaction with the database.
 * This test will create new references in database.
 * Note that the required databases are created create via docker before test execution.
 */
@TestInstance(Lifecycle.PER_CLASS)
class ReferenceInfrastructureTest {

    /**
     * Connect to databases created by docker in gradle test task and create default user.
     */
    @BeforeAll
    fun setup() = registerDefaultUser()

    /**
     * Tries to create a new reference in postgres database.
     */
    @Test
    @Order(1)
    fun testDocumentModifications() {
        runBlocking {
            withTimeout(5000) {
                val info = ClientCreateReferenceDto("test")
                val refRepo = ReferenceRepositoryImpl(defaultUserMail)
                val ref = refRepo.createReference(info)

                assertEquals(info.displayName, ref.displayName)
                assertEquals("", refRepo.downloadContent(ref.uuid))

                val docRepo = DocumentRepositoryImpl("", "")

                docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 0, "line-1", MODIFY))
                assertEquals("line-1", refRepo.downloadContent(ref.uuid))

                docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 1, "line-2", ADD))
                assertEquals("line-1\nline-2", refRepo.downloadContent(ref.uuid))

                docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 0, "line-0", ADD))
                assertEquals("line-0\nline-1\nline-2", refRepo.downloadContent(ref.uuid))

                docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 1, "x", ADD))
                assertEquals("line-0\nx\nline-1\nline-2", refRepo.downloadContent(ref.uuid))

                docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 1, "y", MODIFY))
                assertEquals("line-0\ny\nline-1\nline-2", refRepo.downloadContent(ref.uuid))

                docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 1, "", DELETE))
                assertEquals("line-0\nline-1\nline-2", refRepo.downloadContent(ref.uuid))
            }
        }
    }

}
