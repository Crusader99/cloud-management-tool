package de.hsaalen.cmt

import de.hsaalen.cmt.mongo.MongoDB
import de.hsaalen.cmt.repository.*
import de.hsaalen.cmt.sql.Postgresql
import de.hsaalen.cmt.storage.StorageS3
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
        // Delay on startup to ensure databases have enough time to initialize
        Thread.sleep(5_000)

        MongoDB.configure()
        Postgresql.configure()
        StorageS3.configure()
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
        single<ReferenceRepository> { ReferenceRepositoryImpl }
        single<LabelRepository> { LabelRepositoryImpl }
        single<DocumentRepository> { DocumentRepositoryImpl }
        single<FileRepository> { FileRepositoryImpl }
    }

}
