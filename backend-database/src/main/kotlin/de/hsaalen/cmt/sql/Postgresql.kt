package de.hsaalen.cmt.sql

import de.hsaalen.cmt.environment.*
import de.hsaalen.cmt.sql.schema.*
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * Object for handling postgresql specific tasks.
 */
internal object Postgresql {
    /**
     * Local logger instance for this class.
     */
    private val logger = KotlinLogging.logger { }

    /**
     * Configure postgresql driver with system environment variables and test connection.
     */
    fun configure() {
        if (POSTGRESQL_PASSWORD == DEFAULT_CREDENTIAL_VALUE) {
            logger.warn("Please configure a secure password for postgresql via system environment variables!")
        }

        val url = "jdbc:postgresql://$POSTGRESQL_HOST:$POSTGRESQL_PORT/$POSTGRESQL_DB"
        val driverClass = "org.postgresql.Driver"
        logger.info("Connecting to postgresql using $url")
        Database.connect(url, driver = driverClass, user = POSTGRESQL_USER, password = POSTGRESQL_PASSWORD)
        transaction {
            // Configure self4j logger to simplify debugging
            addLogger(Slf4jSqlDebugLogger)

            // Creates the tables when not existing
            // Also used to test the connection to database
            SchemaUtils.create(UserTable, ReferenceTable, RevisionTable, LabelTable, LabelRefMappingTable)
        }
    }

}
