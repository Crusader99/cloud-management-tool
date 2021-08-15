package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.network.apiPathDownloadDocument
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.rsocket.DocumentChangeDto
import de.hsaalen.cmt.network.session.Client
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.repository.DocumentRepository
import de.hsaalen.cmt.utils.ClientSupport
import io.ktor.http.*

/**
 * Repository port for providing specific document infrastructure. This can be implemented by the server using Mongo db
 * access or implemented for the client to access the server over network. The implementation can be injected using
 * dependency injection.
 */
internal interface DocumentRepositoryImpl : ClientSupport, DocumentRepository {

    /**
     * Send to text edit DTO to server and other clients.
     */
    override suspend fun modifyDocument(request: DocumentChangeDto) {
        Session.instance?.sendLiveDTO(request)
    }

    /**
     * Download the content of a specific reference by uuid.
     */
    override suspend fun downloadDocument(reference: UUID): String {
        val url = Url("$apiEndpoint/$apiPathDownloadDocument/$reference")
        val encryptedText: String = Client.request(url) {
            method = HttpMethod.Get
        }

        // Get key and decrypt line by line
        return encryptedText.lineSequence().decrypt().joinToString("\n")
    }

}
