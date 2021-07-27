package de.hsaalen.cmt.repository

import de.hsaalen.cmt.network.dto.objects.UUID
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

/**
 * Repository port for providing specific file infrastructure. This can be implemented by the server using AWS S3 db
 * access or implemented for the client to access the server over network. The implementation can be injected using
 * dependency injection.
 */
interface FileRepository {

    /**
     * Download the reference content by a specific [UUID].
     */
    suspend fun download(uuid: UUID, contentStream: SendChannel<ByteArray>)

    /**
     * Upload or overwrite the reference content by a specific [UUID].
     */
    suspend fun upload(uuid: UUID, contentStream: ReceiveChannel<ByteArray>, contentLength: Long)

}
