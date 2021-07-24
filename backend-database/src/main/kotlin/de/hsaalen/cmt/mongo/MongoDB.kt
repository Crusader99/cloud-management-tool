package de.hsaalen.cmt.mongo

import de.hsaalen.cmt.environment.*
import mu.KotlinLogging
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

/**
 * Functionality related to MongoDB, which is used for storing documents by their lines.
 */
internal object MongoDB {
    /**
     * Local logger instance for this class.
     */
    private val logger = KotlinLogging.logger { }

    /**
     * The collection where documents are stored.
     */
    var collection: CoroutineCollection<TextDocument>? = null

    /**
     * Configure postgresql driver with system environment variables and test connection.
     */
    fun configure() {
        if (MONGO_PASSWORD == DEFAULT_CREDENTIAL_VALUE) {
            logger.warn("Please configure a secure password for mariadb via system environment variables!")
        }

        val url = "mongodb://$MONGO_USER:$MONGO_PASSWORD@$MONGO_HOST:$MONGO_PORT"
        logger.info("Connecting to $url")
        val client = KMongo.createClient(url).coroutine
        val database = client.getDatabase("test")
        collection = database.getCollection()
        logger.info("Successfully connected to mongodb!")
    }

    /**
     * Create new document by given content.
     */
    suspend fun createDocument(uuid: String, content: String = "") {
        logger.info("Creating new text document in mongo-db...")
        val lines = content.lines().toTypedArray()
        collection?.insertOne(TextDocument(uuid, *lines))
    }

    /**
     * Download the complete content of the document.
     */
    suspend fun getDocumentContent(uuid: String): String {
        val lines = findDocument(uuid).lines
        return lines.joinToString("\n")
    }

    /**
     * Replace the hole content of a document.
     */
    suspend fun replaceDocument(uuid: String, content: String = "") {
        val newLines = content.lines()
        val doc = TextDocument(uuid, newLines)
        collection?.updateOneById(uuid, doc)
    }

    /**
     * Search in collection for document.
     */
    private suspend fun findDocument(uuid: String): TextDocument {
        return collection
            ?.findOne(TextDocument::uuid eq uuid)
            ?: error("Could not find text document with uuid '$uuid' in mongo-db!")
    }
}
