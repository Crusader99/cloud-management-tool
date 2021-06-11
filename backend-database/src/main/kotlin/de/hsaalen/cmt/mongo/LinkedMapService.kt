package de.hsaalen.cmt.mongo

import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto

class LinkedMapService : ServiceTextDocument {
    val documents = mutableMapOf<String, MutableMap<Int, String>>()

    override suspend fun createDocument(uuid: String, content: String) {
        val table = mutableMapOf<Int, String>()
        documents[uuid] = table
        return
    }

    override suspend fun getDocumentContent(uuid: String): ByteArray {
        TODO("Not yet implemented")
    }

    override suspend fun updateDocument(dto: DocumentChangeDto) {
        TODO("Not yet implemented")
    }

    fun getDocument(uuid : String) =  documents[uuid]!!
}
