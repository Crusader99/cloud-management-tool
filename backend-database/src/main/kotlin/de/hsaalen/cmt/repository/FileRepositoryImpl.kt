package de.hsaalen.cmt.repository

import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.storage.StorageS3
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

/**
 * Server implementation of the file repository to provide access to the AWS S3 file storage.
 */
internal object FileRepositoryImpl : FileRepository {

    /**
     * Download the reference content by a specific [UUID].
     */
    override suspend fun download(uuid: UUID, contentStream: SendChannel<ByteArray>) {
        StorageS3.downloadFile(uuid).buffered().use { inputStream ->
            // TODO: implement
//            contentStream.send(inputStream.read)
        }
    }

    /**
     * Upload or overwrite the reference content by a specific [UUID].
     */
    override suspend fun upload(uuid: UUID, contentStream: ReceiveChannel<ByteArray>, contentLength: Long) {
        // TODO: implement
//        StorageS3.uploadFile(uuid)
    }

}
