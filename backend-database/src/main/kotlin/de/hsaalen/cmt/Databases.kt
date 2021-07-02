package de.hsaalen.cmt

import de.hsaalen.cmt.mongo.MongoDB
import de.hsaalen.cmt.repositories.AuthenticationRepositoryImpl
import de.hsaalen.cmt.repository.AuthenticationRepository
import de.hsaalen.cmt.sql.Postgresql
import org.koin.dsl.module

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

    /**
     * Define app dependencies using Koin (dependency injection framework)
     *
     * Further information:
     * - https://betterprogramming.pub/kotlin-and-the-simplest-dependency-injection-tutorial-ever-b437d8c338fe
     * - https://insert-koin.io/docs/quickstart/ktor
     */
    val dependencies = module {
        single<AuthenticationRepository> { AuthenticationRepositoryImpl }
    }


}
