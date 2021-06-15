package de.hsaalen.cmt.mongo

import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto

interface ServiceTextDocument {
    suspend fun createDocument(uuid: String, content: String = "")

    suspend fun getDocumentContent(uuid: String): ByteArray

    suspend fun updateDocument(dto: DocumentChangeDto)
}
