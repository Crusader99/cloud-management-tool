import java.time.Duration

plugins {
    kotlin("js")
    id("org.jetbrains.dokka") // Generate API documentation from source code
}

// Simplify declaration of kotlin wrapper modules
fun kotlinWrapper(module: String) = "org.jetbrains.kotlin-wrappers:kotlin-$module-pre.204-kotlin-1.5.0"

dependencies {
    implementation(project(":common"))

    // React components
    implementation(kotlinWrapper("react:17.0.2"))
    implementation(kotlinWrapper("react-dom:17.0.2"))
    implementation(kotlinWrapper("styled:5.3.0"))

    // Wrapper for material ui components
    // See https://material-ui.com and https://github.com/cfnz/muirwik
    implementation("com.ccfraser.muirwik:muirwik-components:0.8.1")

    // Ensure correct versions of material ui are used
    implementation(npm("@material-ui/core", "4.11.1"))
    implementation(npm("@material-ui/lab", "4.0.0-alpha.56"))
    implementation(npm("@material-ui/icons", "4.9.1"))

    // Test framework on javascript platform
    testImplementation(kotlin("test-js"))
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            // For continuous integration: gradle browserDevelopmentRun --continuous
            distribution {
                directory = File("$buildDir/artifact-js/")
            }
            commonWebpackConfig {
                cssSupport.enabled = true
            }
            runTask { // Route backend requests to Traefik
                val settings = mutableMapOf<String, Any>()
                settings["target"] = "http://localhost:80"
                settings["ws"] = true // Enable websocket support
                val proxyTable = mutableMapOf<String, Any>("/api" to settings)
                devServer = devServer?.copy(port = 8081, proxy = proxyTable)
            }
            testTask {
                timeout.set(Duration.ofSeconds(60L))
                useKarma { // Use Chromium for Linux support
                    useChromiumHeadless()
                }
            }
        }
    }
}
