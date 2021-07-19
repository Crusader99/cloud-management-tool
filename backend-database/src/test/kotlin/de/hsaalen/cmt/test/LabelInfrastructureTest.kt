package de.hsaalen.cmt.test

import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.repository.LabelRepositoryImpl
import de.hsaalen.cmt.repository.ReferenceRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration test to verify the interaction with the database.
 * This test will create new references in database.
 * Note that the required databases are created create via docker before test execution.
 */
@TestInstance(Lifecycle.PER_CLASS)
class LabelInfrastructureTest {

    /**
     * Connect to databases created by docker in gradle test task and create default user.
     */
    @BeforeTest
    fun setupEnvironment() = registerDefaultUser()

    /**
     * Test adding and removing labels related to reference.
     */
    @Test
    fun testLabelModify() {
        val refRepo = ReferenceRepositoryImpl(defaultUserMail)
        val labelRepo = LabelRepositoryImpl(defaultUserMail)
        lateinit var ref: Reference
        val labelName = "test"

        // Create reference
        runBlocking {
            ref = refRepo.createReference(ClientCreateReferenceDto("Reference"))
            logger.info("Created reference: $ref")
        }

        // Add label to reference
        runBlocking {
            labelRepo.addLabel(ref.uuid, labelName)
            logger.info("Added label to reference")
        }

        // Check labels of reference
        runBlocking {
            ref = refRepo.listReferences().references.single()
            assertEquals(labelName, ref.labels.single())
            assertEquals(labelName, labelRepo.listLabels().single())
            logger.info("Label seems to be correct")
        }

        // Remove label
        runBlocking {
            labelRepo.removeLabel(ref.uuid, labelName)
            logger.info("Removed label from reference")
        }

        // Check labels of reference
        runBlocking {
            ref = refRepo.listReferences().references.single()
            assertTrue(ref.labels.isEmpty(), "Reference should not have labels")
            assertTrue(labelRepo.listLabels().isEmpty(), "User should not have labels")
            logger.info("Label correctly removed")
        }
    }

}
