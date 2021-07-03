package de.hsaalen.cmt.repository

import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto

/**
 * Repository port for providing specific document infrastructure. This can be implemented by the server using Mongo db
 * access or implemented for the client to access the server over network. The implementation can be injected using
 * dependency injection.
 */
interface DocumentRepository {

    /**
     * Apply a single modification to the document in repository.
     */
    suspend fun modifyDocument(request: DocumentChangeDto)

}
