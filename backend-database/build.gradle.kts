import com.github.gradledockertests.tasks.DockerRunTask
import com.github.gradledockertests.tasks.DockerStopTask
import com.github.gradledockertests.util.firstPublishedPort
import com.github.gradledockertests.util.freeHostSystemPort

plugins {
    kotlin("jvm") // There are some bugs with Intellij older than 2021.1
    kotlin("plugin.serialization")
    id("com.github.gelangweilte-studenten.gradle-docker-tests") version "1.1.1"
}

kotlin.target {
    compilations.all {
        kotlinOptions.jvmTarget = "1.8"
    }
}

configurations.all {
    // Choose 'jvm' from disambiguating targets
    val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)
    attributes.attribute(attr, "jvm")
}

dependencies {
    implementation(project(":common"))

    // SQL database driver for postgres
    implementation("org.jetbrains.exposed:exposed-core:0.32.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.32.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.32.1")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.32.1")
    implementation("org.postgresql:postgresql:42.2.20")

    // Mongo DB driver to support live edit in text documents
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.2.7")

    // Statistics & logging frameworks
    // See https://github.com/MicroUtils/kotlin-logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.8")

    // JUnit test framework
    testImplementation(kotlin("test"))
}

val dockerPostgres by tasks.registering(DockerRunTask::class) {
    environment["POSTGRES_USER"] = "admin"
    environment["POSTGRES_PASSWORD"] = "admin"
    environment["POSTGRES_DB"] = "postgres"
    addPort(freeHostSystemPort, 5432)
    args("-itd")
    image("postgres")
}

val dockerStop by tasks.registering(DockerStopTask::class) {
    stopContainerFromTask(dockerPostgres)
}

tasks.test {
    dependsOn(dockerPostgres)
    finalizedBy(dockerStop)

    environment["POSTGRESQL_USER"] = "admin"
    environment["POSTGRESQL_PASSWORD"] = "admin"
    environment["POSTGRESQL_DB"] = "postgres"
    environment["POSTGRESQL_PORT"] = dockerPostgres.firstPublishedPort

    useJUnitPlatform()
}
