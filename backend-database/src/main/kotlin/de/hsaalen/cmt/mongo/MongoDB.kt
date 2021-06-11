package de.hsaalen.cmt.mongo

import de.hsaalen.cmt.network.dto.objects.LineChangeMode.*
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import mu.KotlinLogging
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.util.ArrayList

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
        val x = ArrayList<String>()
        x.add(3, "")
        val uuid = dto.uuid
        val line = dto.lineContentEncrypted
        when (dto.lineChangeMode) {
            MODIFY -> collection?.updateOneById(uuid, set(TextDocument::lines.keyProjection(dto.lineNumber) setTo line))
            DELETE -> collection?.updateOneById(uuid, unset(TextDocument::lines.keyProjection(dto.lineNumber)))
//            ADD -> collection?.updateOneById(uuid, push(TextDocument::linesAsArray, line))
        }

//        replaceDocument(dto.uuid, dto.newTextEncrypted) // TODO: update only line update
    }

    suspend fun replaceDocument(uuid: String, content: String = "") {
        val newLines = content.lines()
        val doc = TextDocument(uuid, newLines)
        collection?.updateOneById(uuid, doc)
    }

    private suspend fun findDocument(uuid: String): TextDocument {
        return collection
            ?.findOne(TextDocument::uuid eq uuid)
            ?: throw IllegalStateException("Could not find text document with uuid '$uuid' in mongo-db!")
    }
}
