rootProject.name = "se-project"

pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
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
include(":server-backend")
include(":web-app")

// File local.properties with "sdk.dir" required
if (file("local.properties").exists()) {
    include(":android-app")
} else {
    println("Disabled android build because local.properties file not found!")
}
