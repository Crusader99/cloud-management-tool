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
    kotlin("multiplatform") version "1.5.10" apply false
    kotlin("plugin.serialization") version "1.5.10" apply false

    // Gradle plugin for android development
    id("com.android.application") version "4.1.3" apply false

    // Both plugins required to update versions via "gradle useLatestVersions"
    id("se.patrikerdes.use-latest-versions") version "0.2.16"
    id("com.github.ben-manes.versions") version "0.39.0"

    // Register plugins for better IDE support
    eclipse
    idea
}
