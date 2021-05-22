rootProject.name = "se-project"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.android.application") {
                // Required to use plugin block in build.gradle.kts
                // https://medium.com/@StefMa/its-time-to-ditch-the-buildscript-block-a1ab12e0d9ce
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

include(":common")
include(":backend-server")
include(":web-app")

// File local.properties with "sdk.dir" required
if (file("local.properties").exists()) {
    include(":android-app")
} else {
    println("Disabled android build because local.properties file not found!")
}

// Only include when library project found
// Allows easier debugging without publishing changes
if (file("library-painter").exists()) {
    include(":library-painter")
}
