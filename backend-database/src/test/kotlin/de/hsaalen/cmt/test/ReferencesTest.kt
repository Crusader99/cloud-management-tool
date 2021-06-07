package de.hsaalen.cmt.test

import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.services.ServiceReferences
import de.hsaalen.cmt.services.ServiceUsers
import de.hsaalen.cmt.sql.Postgresql
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test

//import org.junit.FixMethodOrder
//import org.junit.runners.MethodSorters

/**
 * Integration test to verify the interaction with the database.
 * This test will create new references in database.
 * Note that the required databases are created create via docker before test execution
 */
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReferencesTest {

    /**
     * Mail used for tests
     */
    private val userMail = "simon@test.de"

    @BeforeAll
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
    fun test1_ReferenceCreation() {
        runBlocking {
            val info = ClientCreateReferenceDto("test")
            val ref = ServiceReferences.createItem(info, creatorEmail = userMail)
            println(ref)
        }
    }

    @Test
    fun test2_ReferenceList() {

    }

}
