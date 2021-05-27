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
    implementation("org.jetbrains.exposed:exposed-core:0.31.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.31.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.31.1")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.31.1")
    implementation("org.postgresql:postgresql:42.2.20")

    // Statistics & logging frameworks
    // See https://github.com/MicroUtils/kotlin-logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")

    // JUnit test framework
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
