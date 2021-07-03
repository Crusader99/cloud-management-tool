package de.hsaalen.cmt

import de.hsaalen.cmt.mongo.MongoDB
import de.hsaalen.cmt.repository.*
import de.hsaalen.cmt.sql.Postgresql
import org.koin.dsl.module

/**
 * Initialize helper that is accessible from layers above. Contains a module
 * configuration for the koin dependency injection framework.
 */
object DatabaseModules {

    /**
     * Initialize all used database connections.
     */
    fun init() {
        MongoDB.configure()
        Postgresql.configure()
    }

    /**
     * Define app dependencies using Koin (dependency injection framework)
     *
     * Further information:
     * - https://betterprogramming.pub/kotlin-and-the-simplest-dependency-injection-tutorial-ever-b437d8c338fe
     * - https://insert-koin.io/docs/quickstart/ktor
     */
    val dependencies = module {
        single<AuthenticationRepository> { AuthenticationRepositoryImpl }
        factory<ReferencesRepository> { (userEmail: String) -> ReferencesRepositoryImpl(userEmail) }
        single<DocumentRepository> { DocumentRepositoryImpl }
    }


}
