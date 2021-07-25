package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.crypto.encrypt
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.network.utils.ClientSupport
import de.hsaalen.cmt.repository.DocumentRepository

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

}
