package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.apiPathDownloadFile
import de.hsaalen.cmt.network.apiPathUploadFile
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.session.Client
import de.hsaalen.cmt.repository.FileRepository
import de.hsaalen.cmt.utils.ClientSupport
import io.ktor.http.*

/**
 * Provides access to server side file functionality.
 */
internal interface FileRepositoryImpl : ClientSupport, FileRepository {
    /**
     * Download the reference content by a specific [UUID].
     */
    override suspend fun download(uuid: UUID): ByteArray {
        // TODO: remove rSocket code & decrypt data
//        val payload = RequestReferenceDto(uuid).encrypt().buildPayload()
//        val stream = Session.instance?.rSocket?.requestStream(payload) ?: error("Not in session")
//        return stream.map { it.decodeProtobufData<FilePartDto>().decrypt().bytes }
        val url = Url("$apiEndpoint$apiPathDownloadFile/$uuid")
        return Client.request(url) {
            method = HttpMethod.Get
        }
    }

    /**
     * Upload or overwrite the reference content by a specific [UUID].
     */
    override suspend fun upload(uuid: UUID, content: ByteArray) {
        val url = Url("$apiEndpoint$apiPathUploadFile/$uuid")
        return Client.request(url) {
            method = HttpMethod.Post
            body = content
        }
// TODO: remove rSocket code & encrypt data before sending
//        val initPayload = RequestReferenceDto(uuid).encrypt().buildPayload()
//        val stream = contentStream.map { FilePartDto(it).encrypt().buildPayload() }
//        Session.instance?.rSocket?.requestChannel(initPayload, stream) ?: error("Not in session")
    }
}
