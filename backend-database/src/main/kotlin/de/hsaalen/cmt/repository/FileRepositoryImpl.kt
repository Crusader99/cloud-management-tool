package de.hsaalen.cmt.repository

import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.storage.StorageS3

/**
 * Server implementation of the file repository to provide access to the AWS S3 file storage.
 */
internal object FileRepositoryImpl : FileRepository {

    /**
     * Download the reference content by a specific [UUID].
     */
    override suspend fun download(uuid: UUID): ByteArray {
        // TODO: Ensure user has edit permissions for that file
        return StorageS3.downloadFile(uuid).readBytes()
    }

    /**
     * Upload or overwrite the reference content by a specific [UUID].
     */
    override suspend fun upload(uuid: UUID, content: ByteArray) {
        // TODO: Ensure user has edit permissions for that file
        StorageS3.uploadFile(uuid, content.inputStream(), content.size.toLong())
    }

}
