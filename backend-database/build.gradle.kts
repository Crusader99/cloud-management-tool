import com.github.gradledockertests.tasks.DockerRunTask
import com.github.gradledockertests.tasks.DockerStopTask
import com.github.gradledockertests.util.firstPublishedPort
import com.github.gradledockertests.util.freeHostSystemPort
import java.time.Duration

plugins {
    kotlin("jvm") // There are some bugs with Intellij older than 2021.1
    kotlin("plugin.serialization")
    id("com.github.gelangweilte-studenten.gradle-docker-tests")
    id("org.jetbrains.dokka") // Generate API documentation from source code
    id("io.gitlab.arturbosch.detekt") // Code quality analyze tool
}

dependencies {
    implementation(project(":common"))
    implementation(project(":backend-environment"))

    // SQL database driver for postgres
    implementation("org.jetbrains.exposed:exposed-core:0.32.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.32.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.32.1")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.32.1")
    implementation("org.postgresql:postgresql:42.2.23")

    // Mongo DB driver to support live edit in text documents
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.2.8")

    // Import Amazon AWS S3 driver for accessing Minio file storage
    implementation("software.amazon.awssdk:s3:2.17.7")

    // Redis for synchronisation between server multiple instances
    implementation("org.redisson:redisson:3.16.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.2.2")

    // JUnit test framework
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.0")
}

configurations.all {
    // Choose 'jvm' from disambiguating targets
    val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)
    attributes.attribute(attr, "jvm")
}

val dockerPostgres by tasks.registering(DockerRunTask::class) {
    environment["POSTGRES_USER"] = "admin123"
    environment["POSTGRES_PASSWORD"] = "admin123"
    environment["POSTGRES_DB"] = "postgres"
    addPort(freeHostSystemPort, 5432)
    args("-itd")
    image("postgres:13.3-alpine")
}

val dockerMongoDB by tasks.registering(DockerRunTask::class) {
    environment["MONGO_INITDB_ROOT_USERNAME"] = "admin123"
    environment["MONGO_INITDB_ROOT_PASSWORD"] = "admin123"
    addPort(freeHostSystemPort, 27017)
    args("-itd")
    image("mongo:4.4")
}

val dockerMinio by tasks.registering(DockerRunTask::class) {
    environment["MINIO_ROOT_USER"] = "admin123"
    environment["MINIO_ROOT_PASSWORD"] = "admin123"
    addPort(freeHostSystemPort, 9000)
    args("-itd")
    image("minio/minio")
    command("server /data")
}

val dockerRedis by tasks.registering(DockerRunTask::class) {
    addPort(freeHostSystemPort, 6379)
    args("-itd")
    image("redis")
}

val dockerStop by tasks.registering(DockerStopTask::class) {
    stopContainerFromTask(dockerPostgres)
    stopContainerFromTask(dockerMongoDB)
    stopContainerFromTask(dockerMinio)
    stopContainerFromTask(dockerRedis)
}

tasks.test {
    dependsOn(dockerPostgres, dockerMongoDB, dockerMinio, dockerRedis)
    finalizedBy(dockerStop)

    environment["PASSWORD_SALT"] = "salt"

    environment["POSTGRESQL_USER"] = "admin123"
    environment["POSTGRESQL_PASSWORD"] = "admin123"
    environment["POSTGRESQL_DB"] = "postgres"
    environment["POSTGRESQL_PORT"] = dockerPostgres.firstPublishedPort

    environment["MONGO_USER"] = "admin123"
    environment["MONGO_PASSWORD"] = "admin123"
    environment["MONGO_PORT"] = dockerMongoDB.firstPublishedPort

    environment["S3_ENDPOINT"] = "http://localhost:" + dockerMinio.firstPublishedPort
    environment["REDIS_PORT"] = dockerRedis.firstPublishedPort

    useJUnitPlatform()
    timeout.set(Duration.ofSeconds(60L))
}

// Configure detekt code analyze tool to generate HTML report
detekt {
    ignoreFailures = true // Currently only print warning
    reports {
        html.enabled = true
    }
}
