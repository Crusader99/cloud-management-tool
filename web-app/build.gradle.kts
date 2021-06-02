plugins {
    kotlin("js")
}

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    jcenter() // For muirwik-components
}

// Simplify declaration of kotlin wrapper modules
fun kotlinWrapper(module: String) = "org.jetbrains:kotlin-$module-pre.153-kotlin-1.4.32"

dependencies {
    implementation(project(":common"))

    // React components
    implementation(kotlinWrapper("react:17.0.2"))
    implementation(kotlinWrapper("react-dom:17.0.2"))
    implementation(kotlinWrapper("react-table:7.6.3")) // new: 7.7.0
    implementation(kotlinWrapper("styled:5.2.3")) // new: 5.3.0

    implementation("com.ccfraser.muirwik:muirwik-components:0.6.7")

    implementation("net.subroh0508.kotlinmaterialui:core:0.5.6")
    implementation("net.subroh0508.kotlinmaterialui:lab:0.5.6")

    // Test framework on javascript platform
    testImplementation(kotlin("test-js"))
}

kotlin {
    js(LEGACY) {
        useCommonJs()
        binaries.executable()
        browser {
            // For continuous integration: gradle browserDevelopmentRun --continuous
            distribution {
                directory = File("$buildDir/artifact-js/")
            }
            commonWebpackConfig {
                cssSupport.enabled = true
            }
            runTask {
                val proxyTable = mutableMapOf<String, Any>("/api" to "http://localhost:80")
                devServer = devServer?.copy(port = 8081, proxy = proxyTable)
            }
        }
    }
}
