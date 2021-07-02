package de.hsaalen.cmt.test

import de.hsaalen.cmt.mongo.MongoDB
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.objects.LineChangeMode.*
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.repositories.ReferencesRepositoryImpl
import de.hsaalen.cmt.repositories.AuthenticationRepositoryImpl
import de.hsaalen.cmt.sql.Postgresql
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration test to verify the interaction with the database.
 * This test will create new references in database.
 * Note that the required databases are created create via docker before test execution.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReferencesTest {

    /**
     * Mail used for tests
     */
    private val userMail = "simon@test.de"

    /**
     * Connect to databases created by docker in gradle test task.
     */
    @BeforeAll
    fun setup() {
        runBlocking {
            // Delay on startup to ensure databases have enough time to initialize
            delay(5_000)
            Postgresql.configure()
            MongoDB.configure()
            AuthenticationRepositoryImpl.register("Simon", userMail, "12345678")
        }
    }

    /**
     * Tries to create a new reference in postgres database.
     */
    @Test
    @Order(1)
    fun test1_ReferenceCreation() {
        runBlocking {
            withTimeout(5000) {
                val info = ClientCreateReferenceDto("test")
                val ref = ReferencesRepositoryImpl.createItem(info, creatorEmail = userMail)
                assertEquals(info.displayName, ref.displayName)
                assertEquals("", MongoDB.getDocumentContent(ref.uuid))

                MongoDB.updateDocument(DocumentChangeDto(ref.uuid, 0, "line-1", MODIFY))
                assertEquals("line-1", MongoDB.getDocumentContent(ref.uuid))

                MongoDB.updateDocument(DocumentChangeDto(ref.uuid, 1, "line-2", ADD))
                assertEquals("line-1\nline-2", MongoDB.getDocumentContent(ref.uuid))

                MongoDB.updateDocument(DocumentChangeDto(ref.uuid, 0, "line-0", ADD))
                assertEquals("line-0\nline-1\nline-2", MongoDB.getDocumentContent(ref.uuid))

                MongoDB.updateDocument(DocumentChangeDto(ref.uuid, 1, "x", ADD))
                assertEquals("line-0\nx\nline-1\nline-2", MongoDB.getDocumentContent(ref.uuid))

                MongoDB.updateDocument(DocumentChangeDto(ref.uuid, 1, "y", MODIFY))
                assertEquals("line-0\ny\nline-1\nline-2", MongoDB.getDocumentContent(ref.uuid))

                MongoDB.updateDocument(DocumentChangeDto(ref.uuid, 1, "", DELETE))
                assertEquals("line-0\nline-1\nline-2", MongoDB.getDocumentContent(ref.uuid))
            }
        }
    }

}
