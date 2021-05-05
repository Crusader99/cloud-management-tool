plugins {
    kotlin("js")
}

repositories {
    maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
    mavenCentral()
}

dependencies {
    implementation(project(":common"))

    // React components
    implementation("org.jetbrains:kotlin-react:17.0.2-pre.153-kotlin-1.4.32")
    implementation("org.jetbrains:kotlin-react-dom:17.0.2-pre.153-kotlin-1.4.32")
    implementation("org.jetbrains:kotlin-styled:5.2.1-pre.148-kotlin-1.4.30")

    implementation("com.ccfraser.muirwik:muirwik-components:0.6.7")

    implementation("net.subroh0508.kotlinmaterialui:core:0.5.6")
    implementation("net.subroh0508.kotlinmaterialui:lab:0.5.6")

    // Test framework on javascript platform
    testImplementation(kotlin("test-js"))
}

kotlin {
    js {
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
                devServer = devServer?.copy(port = 8081)
            }
        }
    }
}