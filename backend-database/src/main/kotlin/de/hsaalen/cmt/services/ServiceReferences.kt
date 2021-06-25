package de.hsaalen.cmt.services

import de.hsaalen.cmt.mongo.MongoDB
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.sql.schema.ReferenceDao
import de.hsaalen.cmt.sql.schema.RevisionDao
import de.hsaalen.cmt.sql.schema.UserDao
import de.hsaalen.cmt.sql.schema.UserTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.joda.time.DateTime
import java.io.InputStream
import java.util.*

/**
 * Handles database operations for the reference and revision management.
 */
object ServiceReferences {

    /**
     * Create a new reference to a first revision.
     */
    suspend fun createItem(
        info: ClientCreateReferenceDto,
        creatorEmail: String,
    ): Reference {
        val ref: Reference = newSuspendedTransaction {
            val creator = UserDao.find(UserTable.email eq creatorEmail)
                .singleOrNull()
                ?: throw SecurityException("User $creatorEmail not found!")
            val now = DateTime.now()
            val reference = ReferenceDao.new {
                this.accessCode = "ACCESS_CODE"
                this.displayName = info.displayName
                this.contentType = "document"
            }
            val revision = RevisionDao.new {
                this.item = reference
                this.index = 0

                this.dateCreation = now
                this.dateLastAccess = now
                this.comment = info.comment
                this.creator = creator
                this.accessCount = 0
            }
            Reference(
                uuid = reference.id.toString(),
                accessCode = reference.accessCode,
                displayName = reference.displayName,
                contentType = reference.contentType,
                dateCreation = revision.dateCreation.millis,
                dateLastAccess = revision.dateLastAccess.millis,
                labels = listOf("Not implemented yet")
            )
        }
        MongoDB.createDocument(ref.uuid, info.content)
        return ref
    }

    suspend fun listReferences(query: ClientReferenceQueryDto): ServerReferenceListDto {
        val refs = newSuspendedTransaction {
            // TODO: filter only files that user owns
            ReferenceDao.all().map { it.toReference() }
        }
        return ServerReferenceListDto(refs)
    }

    suspend fun downloadContent(uuid: String): InputStream {
        return MongoDB.getDocumentContent(uuid).byteInputStream()
    }

    /**
     * Delete a reference by the given reference uuid.
     */
    suspend fun deleteReferences(uuid: String) {
        newSuspendedTransaction {
            ReferenceDao.findById(UUID.fromString(uuid))
                ?.delete()
                ?: throw IllegalArgumentException("No reference with uuid=$uuid found!")
        }
    }

}
