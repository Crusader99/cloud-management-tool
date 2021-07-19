package de.hsaalen.cmt.test

import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.repository.LabelRepositoryImpl
import de.hsaalen.cmt.repository.ReferenceRepositoryImpl
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import kotlin.test.*

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
    fun setupEnvironment() = registerTestUser()

    /**
     * Test adding and removing labels related to reference.
     */
    @Test
    fun testLabelModify() {
        val refRepo = ReferenceRepositoryImpl
        val labelRepo = LabelRepositoryImpl
        lateinit var ref: Reference
        val labelName = "test"

        // Create reference
        runBlockingWithSession {
            ref = refRepo.createReference(ClientCreateReferenceDto("Reference"))
            logger.info("Created reference: $ref")
        }

        // Add label to reference
        runBlockingWithSession {
            labelRepo.addLabel(ref, labelName)
            logger.info("Added label to reference")
        }

        lateinit var previousLabels: List<String>

        // Check labels of reference
        runBlockingWithSession {
            ref = refRepo.listReferences().references.single { it.uuid == ref.uuid }
            assertEquals(labelName, ref.labels.single())
            previousLabels = labelRepo.listLabels()
            assertContains(previousLabels, labelName, "Invalid number of labels")
            logger.info("Label seems to be correct")
        }

        // Remove label
        runBlockingWithSession {
            labelRepo.removeLabel(ref, labelName)
            logger.info("Removed label from reference")
        }

        // Check labels of reference
        runBlockingWithSession {
            ref = refRepo.listReferences().references.single { it.uuid == ref.uuid }
            assertTrue(ref.labels.isEmpty(), "Reference should not have labels")
            assertTrue(previousLabels.size > labelRepo.listLabels().size, "Invalid number of labels")
            logger.info("Label correctly removed")
        }
    }

    /**
     * Test what happens when multiple documents with labels exist.
     */
    @Test
    fun testLabelConflict() = runBlockingWithSession {
        val refRepo = ReferenceRepositoryImpl
        val labelRepo = LabelRepositoryImpl

        // Create references with default label
        val ref1 = refRepo.createReference(ClientCreateReferenceDto("Reference1", labels = listOf("1")))
        val ref2 = refRepo.createReference(ClientCreateReferenceDto("Reference2", labels = listOf("2")))

        // Replace labels
        labelRepo.addLabel(ref1, "3")
        labelRepo.addLabel(ref2, "1")
        labelRepo.removeLabel(ref1, "1")
        labelRepo.removeLabel(ref2, "2")

        // Validate labels correct
        assertContentEquals(listOf("3"), refRepo.listReferences().references.single { it.uuid == ref1.uuid }.labels)
        assertContentEquals(listOf("1"), refRepo.listReferences().references.single { it.uuid == ref2.uuid }.labels)
    }

}
