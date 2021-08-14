package de.hsaalen.cmt.repository

import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.session.currentSession
import de.hsaalen.cmt.sql.schema.ReferenceDao
import de.hsaalen.cmt.storage.StorageS3
import de.hsaalen.cmt.utils.id
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Server implementation of the file repository to provide access to the AWS S3 file storage.
 */
internal object FileRepositoryImpl : FileRepository {

    /**
     * Download the reference content by a specific [UUID].
     */
    override suspend fun download(uuid: UUID): ByteArray {
        checkHasPermissions(currentSession.userMail, uuid)
        return StorageS3.downloadFile(uuid).readBytes()
    }

    /**
     * Upload or overwrite the reference content by a specific [UUID].
     */
    override suspend fun upload(reference: UUID, content: ByteArray) {
        checkHasPermissions(currentSession.userMail, reference)
        StorageS3.uploadFile(reference, content.inputStream(), content.size.toLong())
    }

    /**
     * Ensure user has edit permissions for that file to upload/download.
     */
    private suspend fun checkHasPermissions(userMail: String, reference: UUID) = newSuspendedTransaction {
        val ref = ReferenceDao.findById(reference.id) ?: error("No reference with uuid=$reference found!")
        if (ref.owner.email != userMail) {
            throw SecurityException("Can not access references from different users!")
        }
    }

}
