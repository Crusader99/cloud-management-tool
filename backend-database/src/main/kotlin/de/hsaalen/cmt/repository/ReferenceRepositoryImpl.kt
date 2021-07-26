package de.hsaalen.cmt.repository

import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.mongo.MongoDB
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.ContentType
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.network.dto.websocket.ReferenceUpdateAddDto
import de.hsaalen.cmt.network.dto.websocket.ReferenceUpdateRemoveDto
import de.hsaalen.cmt.session.currentSession
import de.hsaalen.cmt.sql.schema.ReferenceDao
import de.hsaalen.cmt.sql.schema.ReferenceTable
import de.hsaalen.cmt.sql.schema.RevisionDao
import de.hsaalen.cmt.sql.schema.UserDao
import de.hsaalen.cmt.storage.StorageS3
import de.hsaalen.cmt.utils.id
import de.hsaalen.cmt.utils.toUUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.joda.time.DateTime

/**
 * Handles database operations for the reference and revision management.
 */
internal object ReferenceRepositoryImpl : ReferenceRepository {

    /**
     * E-Mail address of the current user session.
     */
    private val userEmail: String
        get() = currentSession.userMail

    /**
     * Send request to repository for creating a new reference.
     */
    override suspend fun createReference(request: ClientCreateReferenceDto): Reference {
        val ref: Reference = newSuspendedTransaction {
            // Create document in SQL
            val creator = UserDao.findUserByEmail(userEmail)
            val now = DateTime.now()
            val reference = ReferenceDao.new {
                this.accessCode = "ACCESS_CODE"
                this.displayName = request.displayName
                this.contentType = request.contentType
                this.owner = creator
            }
            val revision = RevisionDao.new {
                this.item = reference
                this.index = 0

                this.dateCreation = now
                this.dateLastAccess = now
                this.creator = creator
                this.accessCount = 0
            }

            Reference(
                uuid = reference.id.toUUID(),
                accessCode = reference.accessCode,
                displayName = reference.displayName,
                contentType = reference.contentType,
                dateCreation = revision.dateCreation.millis,
                dateLastAccess = revision.dateLastAccess.millis,
                labels = request.labels
            )
        }

        // Create document in mongo-db
        try {
            MongoDB.createDocument(ref.uuid.value, request.documentLines)
        } catch (ex: Exception) {
            deleteReference(ref) // Cleanup also in SQL on failure
            throw IllegalStateException("Unable to create document for new reference", ex)
        }

        // Add labels
        try {
            for (label in request.labels) {
                LabelRepositoryImpl.addLabel(ref.uuid, label)
            }
        } catch (ex: Exception) {
            deleteReference(ref) // Cleanup also in SQL on failure
            throw IllegalStateException("Unable to add labels for new reference", ex)
        }

        // Call event handlers
        GlobalEventDispatcher.notify(ReferenceUpdateAddDto(ref))
        return ref
    }

    /**
     * Provide a list of all related references to search query.
     */
    override suspend fun listReferences(query: ClientReferenceQueryDto): ServerReferenceListDto {
        val refs = newSuspendedTransaction {
            val creator = UserDao.findUserByEmail(userEmail)
            ReferenceDao.find(ReferenceTable.owner eq creator.id).map { it.toReference() }
        }
        return ServerReferenceListDto(refs)
    }

    /**
     * Delete a reference by the given reference [UUID].
     */
    override suspend fun deleteReference(request: ClientDeleteReferenceDto) {
        val uuid = request.uuid
        lateinit var contentType: ContentType

        // Delete reference from SQL database
        newSuspendedTransaction {
            val ref = ReferenceDao.findById(uuid.id) ?: error("No reference with uuid=$uuid found!")
            if (ref.owner.email != userEmail) {
                throw SecurityException("Can not delete references from different users!")
            }

            contentType = ref.contentType
            ref.delete()
        }

        try {
            when (contentType) {
                ContentType.TEXT -> MongoDB.deleteDocument(uuid.value)  // Delete document content from MongoDB
                ContentType.FILE -> StorageS3.deleteFile(uuid)  // Delete related file from S3 storage.
            }
        } catch (ex: Exception) {
            throw IllegalStateException("Unable to delete reference content", ex)
        } finally {
            // Call event handlers
            GlobalEventDispatcher.notify(ReferenceUpdateRemoveDto(uuid))
        }
    }

}
