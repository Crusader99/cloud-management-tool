package de.hsaalen.cmt.test

import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.objects.ContentType
import de.hsaalen.cmt.network.dto.objects.LineChangeMode.*
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.rsocket.DocumentChangeDto
import de.hsaalen.cmt.repository.DocumentRepositoryImpl
import de.hsaalen.cmt.repository.ReferenceRepositoryImpl
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
    private lateinit var ref: Reference

    /**
     * Connect to databases created by docker in gradle test task and create default user.
     */
    @BeforeAll
    fun setup() = registerTestUser()

    /**
     * Tries to create a new reference in postgres database.
     */
    @Test
    @Order(0)
    fun testCreateReferenceWithLabels() {
        runBlockingWithSession {
            val labelNames = setOf("a", "b", "c")
            val info = ClientCreateReferenceDto("test", contentType = ContentType.TEXT, labels = labelNames)
            val refRepo = ReferenceRepositoryImpl
            val docRepo = DocumentRepositoryImpl
            ref = refRepo.createReference(info)

            suspend fun validate() {
                assertEquals(info.displayName, ref.displayName)
                assertEquals("", docRepo.downloadDocument(ref.uuid))
                assertEquals(info.labels, labelNames)
            }

            validate()
            ref = refRepo.listReferences().references.single { it.uuid == ref.uuid }
            validate()
        }
    }

    /**
     * Tries to create a new reference in postgres database.
     */
    @Test
    @Order(1)
    fun testDocumentModifications() {
        runBlockingWithSession {
            val refRepo = ReferenceRepositoryImpl
            val docRepo = DocumentRepositoryImpl
            ref = refRepo.listReferences().references.single { it.uuid == ref.uuid }

            docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 0, "line-1", MODIFY))
            assertEquals("line-1", docRepo.downloadDocument(ref.uuid))

            docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 1, "line-2", ADD))
            assertEquals("line-1\nline-2", docRepo.downloadDocument(ref.uuid))

            docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 0, "line-0", ADD))
            assertEquals("line-0\nline-1\nline-2", docRepo.downloadDocument(ref.uuid))

            docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 1, "x", ADD))
            assertEquals("line-0\nx\nline-1\nline-2", docRepo.downloadDocument(ref.uuid))

            docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 1, "y", MODIFY))
            assertEquals("line-0\ny\nline-1\nline-2", docRepo.downloadDocument(ref.uuid))

            docRepo.modifyDocument(DocumentChangeDto(ref.uuid, 1, "", DELETE))
            assertEquals("line-0\nline-1\nline-2", docRepo.downloadDocument(ref.uuid))
        }
    }

}
