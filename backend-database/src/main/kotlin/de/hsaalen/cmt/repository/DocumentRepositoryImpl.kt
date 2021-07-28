package de.hsaalen.cmt.repository

import com.mongodb.client.model.PushOptions
import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.events.server.UserDocumentChangeEvent
import de.hsaalen.cmt.mongo.MongoDB
import de.hsaalen.cmt.mongo.TextDocument
import de.hsaalen.cmt.network.dto.objects.LineChangeMode.*
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.rsocket.DocumentChangeDto
import de.hsaalen.cmt.session.currentSession
import de.hsaalen.cmt.session.senderSocketId
import de.hsaalen.cmt.sql.schema.ReferenceDao
import de.hsaalen.cmt.utils.id
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.litote.kmongo.*

/**
 * Server implementation of the document repository using a Mongo database.
 */
internal object DocumentRepositoryImpl : DocumentRepository {

    /**
     * Apply a single modification to the document in repository.
     */
    override suspend fun modifyDocument(request: DocumentChangeDto) {
        val c = MongoDB.collection ?: return
        val id = request.uuid

        // Ensure user has permissions to access this document
        checkAccess(currentSession.userMail, id)

        // Modify document in MongoDB
        val newLine = request.lineContent
        val allLines = TextDocument::lines
        val targetLine = allLines.colProperty.memberWithAdditionalPath(request.lineNumber.toString())
        when (request.lineChangeMode) {
            MODIFY -> c.updateOneById(id, set(targetLine setTo newLine))
            ADD -> c.updateOneById(id, pushEach(allLines, listOf(newLine), PushOptions().position(request.lineNumber)))
            DELETE -> {
                c.updateOneById(id, unset(targetLine))
                c.updateOneById(id, pull(allLines, null))
            }
        }

        // Notify event listeners
        val event = UserDocumentChangeEvent(request, currentSession.userMail, currentSession.senderSocketId)
        GlobalEventDispatcher.notify(event)
    }

    /**
     * Download the content of a specific reference by uuid.
     */
    override suspend fun downloadContent(uuid: UUID): String {
        // Ensure user has permissions to access this document
        checkAccess(currentSession.userMail, uuid)

        // Read document content from MongoDB
        return MongoDB.getDocumentContent(uuid.value)
    }

    /**
     * Ensure user has permissions to access the given reference content. Will throw an exception
     * when user has no permissions to access the reference.
     */
    private suspend fun checkAccess(userMail: String, reference: UUID) {
        newSuspendedTransaction {
            val ref = ReferenceDao.findById(reference.id) ?: error("Reference not found: $reference")
            check(ref.owner.email == userMail) { "No permissions to access document" }
        }
    }

}
