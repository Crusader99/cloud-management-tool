allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://provider.ddnss.de/repository/")
    }

    group = "de.hsaalen.cmt"
    version = "1.0.0"

    apply(plugin = "se.patrikerdes.use-latest-versions")
    apply(plugin = "com.github.ben-manes.versions")
}

plugins {
    kotlin("multiplatform") version "1.5.31" apply false // Allows common projects
    kotlin("plugin.serialization") version "1.5.21" apply false // Support for json serialization

    // Gradle plugin for android development
    id("com.android.application") version "4.1.3" apply false

    // Both plugins required to update versions via "gradle useLatestVersions"
    id("se.patrikerdes.use-latest-versions") version "0.2.17"
    id("com.github.ben-manes.versions") version "0.39.0"

    // Self written plugin (not part of this project) which is used to execute docker containers while gradle build
    // This plugin was written because of issues in the existing docker gradle plugins available
    id("com.github.gelangweilte-studenten.gradle-docker-tests") version "1.2.6" apply false

    // Code quality: Tool for static code analysis
    id("io.gitlab.arturbosch.detekt") version "1.18.1" apply false

    // Generate API documentation from source code
    id("org.jetbrains.dokka") version "1.5.30"

    // Register plugins for better IDE support
    eclipse
    idea
}

// Configure API doc generator
tasks.dokkaHtmlMultiModule {
    outputDirectory.set(buildDir.resolve("api-doc"))
    includes.from("README.md") // Print on index page
}

// Execute this task to generate API documentation.
val docs by tasks.registering {
    dependsOn(tasks.dokkaHtmlMultiModule)
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach { dependsOn("assemble") }
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach { dependsOn("assemble") }
