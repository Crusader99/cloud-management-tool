package de.hsaalen.cmt.mongo

import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import mu.KotlinLogging
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

object MongoDB {
    private val logger = KotlinLogging.logger { }

    private var collection: CoroutineCollection<TextDocument>? = null

    /**
     * Configure postgresql driver with system environment variables and test connection.
     */
    fun configure() {
        val url = "mongodb://admin:admin@mongodb" // TODO: read from environment variables
        val client = KMongo.createClient(url).coroutine
        val database = client.getDatabase("test")
        collection = database.getCollection()
    }

    suspend fun createDocument(uuid: String, content: String = "") {
        val lines = content.lines().toTypedArray()
        collection?.insertOne(TextDocument(uuid, *lines))
    }

    suspend fun getDocumentContent(uuid: String): ByteArray {
        return findDocument(uuid).linesAsArray.joinToString("\n").encodeToByteArray()
    }

    suspend fun updateDocument(dto: DocumentChangeDto) {
        replaceDocument(dto.uuid, dto.newTextEncrypted) // TODO: update only line update
    }

    suspend fun replaceDocument(uuid: String, content: String = "") {
        val doc = findDocument(uuid)
        val newLines = content.lines()
        doc.linesAsArray = newLines
        collection?.updateOneById(uuid, doc)
    }

    private suspend fun findDocument(uuid: String): TextDocument {
        return collection
            ?.findOne(TextDocument::uuid eq uuid)
            ?: throw IllegalStateException("Could not find text document with uuid '$uuid' in mongo-db!")
    }
}
