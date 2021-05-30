package de.hsaalen.cmt.services

import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.sql.schema.ReferenceDao
import de.hsaalen.cmt.sql.schema.RevisionDao
import de.hsaalen.cmt.sql.schema.UserDao
import de.hsaalen.cmt.sql.schema.UserTable
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.joda.time.DateTime
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Handles database operations for the reference and revision management.
 */
object ServiceReferences {

    /**
     * Create a new reference to a first revision.
     */
    suspend fun createItem(
        displayName: String,
        creatorEmail: String,
        comment: String = ""
    ): Reference {
        return newSuspendedTransaction {
            val creator = UserDao.find { UserTable.email eq creatorEmail }.single()
            val now = DateTime.now()
            val reference = ReferenceDao.new {
                this.accessCode = "ACCESS_CODE"
                this.displayName = displayName
                this.contentType = "document"
            }
            val revision = RevisionDao.new {
                this.item = reference
                this.index = 0

                this.dateCreation = now
                this.dateLastAccess = now
                this.comment = comment
                this.creator = creator
                this.accessCount = 0
            }
            Reference(
                accessCode = reference.accessCode,
                displayName = reference.displayName,
                contentType = reference.contentType,
                dateCreation = revision.dateCreation.millis,
                dateLastAccess = revision.dateLastAccess.millis,
                labels = listOf("Not implemented yet")
            )
        }
    }

    suspend fun listReferences(query: ClientReferenceQueryDto): ServerReferenceListDto {
        val refs = mutableListOf<Reference>()

//        ReferenceDao.find {  }

        // Currently create only dummy objects
        // TODO: replace with actual db query
        repeat(10 + Random.nextInt(100)) { index ->
            val accessCode = Random.nextInt('A'.toInt()..'Z'.toInt()).toChar().toString()
            val displayName = "File-Ref-$index"
            val now = System.currentTimeMillis()
            val labels = listOf("note")
            refs += Reference(accessCode, displayName, "text", now, now, labels)
        }
        return ServerReferenceListDto(refs)
    }

}
