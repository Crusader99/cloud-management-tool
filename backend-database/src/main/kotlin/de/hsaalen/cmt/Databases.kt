package de.hsaalen.cmt

import de.hsaalen.cmt.mongo.MongoDB
import de.hsaalen.cmt.sql.Postgresql

/**
 * Initialize helper that is accessible from layers above.
 * TODO: may replaced with DI later
 */
object Databases {

    /**
     * Initialize all used database connections.
     */
    fun init() {
        MongoDB.configure()
        Postgresql.configure()
    }

}
