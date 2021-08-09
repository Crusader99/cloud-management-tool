import java.time.Duration

plugins {
    kotlin("js")
    id("org.jetbrains.dokka") // Generate API documentation from source code
    id("io.gitlab.arturbosch.detekt") // Code quality analyze tool
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
    implementation("com.ccfraser.muirwik:muirwik-components:0.8.2")

    // Test framework on javascript platform
    testImplementation(kotlin("test-js"))
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            // For continuous integration: gradle browserDevelopmentRun --continuous
            useCommonJs()
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

// Configure detekt code analyze tool to generate HTML report
detekt {
    ignoreFailures = true // Currently only print warning
    reports {
        html.enabled = true
    }
}
