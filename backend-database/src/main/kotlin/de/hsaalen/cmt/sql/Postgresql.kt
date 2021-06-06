package de.hsaalen.cmt.sql

import de.hsaalen.cmt.environment.*
import de.hsaalen.cmt.sql.schema.ReferenceTable
import de.hsaalen.cmt.sql.schema.RevisionTable
import de.hsaalen.cmt.sql.schema.LabelTable
import de.hsaalen.cmt.sql.schema.UserTable
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction


/**
 * Object for handling postgresql specific tasks.
 */
object Postgresql {
    private val logger = KotlinLogging.logger { }

    /**
     * Configure postgresql driver with system environment variables and test connection.
     */
    fun configure() {
        val url = "jdbc:postgresql://$POSTGRESQL_HOST:$POSTGRESQL_PORT/$POSTGRESQL_DB"
        val driverClass = "org.postgresql.Driver"
        logger.info("Connecting to postgresql using $url")
        Database.connect(url, driver = driverClass, user = POSTGRESQL_USER, password = POSTGRESQL_PASSWORD)
        transaction {
            // Configure de.hsaalen.cmt.sql logger to simplify debugging
            addLogger(Slf4jSqlDebugLogger)

            // Creates the tables when not existing
            // Also used to test the connection to database
            SchemaUtils.create(UserTable, ReferenceTable, RevisionTable, LabelTable)
        }
    }

}
