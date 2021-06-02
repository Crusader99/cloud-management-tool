package de.hsaalen.cmt.test

import de.hsaalen.cmt.services.ServiceReferences
import de.hsaalen.cmt.services.ServiceUsers
import de.hsaalen.cmt.sql.Postgresql
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Integration test to verify the interaction with the database.
 * This test will create new references in database.
 * Note that the required databases are created create via docker before test execution
 */
class ReferencesTest {

    /**
     * Mail used for tests
     */
    private val userMail = "simon@test.de"

    @BeforeTest
    fun setup() {
        Postgresql.configure()
        runBlocking {
            ServiceUsers.register("Simon", userMail, "12345678")
        }
    }

    /**
     * Tries to create a new reference in postgres database.
     */
    @Test
    fun testReferenceCreation() {
        runBlocking {
            val ref = ServiceReferences.createItem("test", creatorEmail = userMail)
            println(ref)
        }
    }

}
