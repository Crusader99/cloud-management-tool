package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.crypto.encrypt
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
    override suspend fun download(reference: UUID): ByteArray {
        val url = Url("$apiEndpoint$apiPathDownloadFile/$reference")
        val encryptedContent: ByteArray = Client.request(url) {
            method = HttpMethod.Get
        }
        return decrypt(encryptedContent)
    }

    /**
     * Upload or overwrite the reference content by a specific [UUID].
     */
    override suspend fun upload(reference: UUID, content: ByteArray) {
        val url = Url("$apiEndpoint$apiPathUploadFile/$reference")
        val encryptedContent = encrypt(content)
        return Client.request(url) {
            method = HttpMethod.Post
            headers.remove(HttpHeaders.ContentType) // Prevent JSON header
            body = encryptedContent
        }
    }
}
