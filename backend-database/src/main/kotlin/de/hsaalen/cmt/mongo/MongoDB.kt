package de.hsaalen.cmt.mongo

import com.mongodb.client.model.PushOptions
import de.hsaalen.cmt.environment.MONGO_HOST
import de.hsaalen.cmt.environment.MONGO_PASSWORD
import de.hsaalen.cmt.environment.MONGO_PORT
import de.hsaalen.cmt.environment.MONGO_USER
import de.hsaalen.cmt.network.dto.objects.LineChangeMode.*
import de.hsaalen.cmt.network.dto.websocket.DocumentChangeDto
import mu.KotlinLogging
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object MongoDB {
    private val logger = KotlinLogging.logger { }

    private var collection: CoroutineCollection<TextDocument>? = null

    /**
     * Configure postgresql driver with system environment variables and test connection.
     */
    fun configure() {
        val url = "mongodb://$MONGO_USER:$MONGO_PASSWORD@$MONGO_HOST:$MONGO_PORT"
        logger.info("Connecting to $url")
        val client = KMongo.createClient(url).coroutine
        val database = client.getDatabase("test")
        collection = database.getCollection()
        logger.info("Successfully connected to mongodb!")
    }

    suspend fun createDocument(uuid: String, content: String = "") {
        logger.info("Creating new text document in mongo-db...")
        val lines = content.lines().toTypedArray()
        collection?.insertOne(TextDocument(uuid, *lines))
    }

    suspend fun getDocumentContent(uuid: String): String {
        val lines = findDocument(uuid).lines
        return lines.joinToString("\n")
    }

    suspend fun updateDocument(dto: DocumentChangeDto) {
        val c = collection ?: return
        val id = dto.uuid
        val line = dto.lineContentEncrypted
        val documentLines = TextDocument::lines
        val targetLine = documentLines.colProperty.memberWithAdditionalPath(dto.lineNumber.toString())
        when (dto.lineChangeMode) {
            MODIFY -> c.updateOneById(id, set(targetLine setTo line))
            ADD -> c.updateOneById(id, pushEach(documentLines, listOf(line), PushOptions().position(dto.lineNumber)))
            DELETE -> {
                c.updateOneById(id, unset(targetLine))
                c.updateOneById(id, pull(documentLines, null))
            }
        }
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
