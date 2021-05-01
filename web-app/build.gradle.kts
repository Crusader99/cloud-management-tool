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
    implementation("org.jetbrains:kotlin-react:17.0.2-pre.154-kotlin-1.5.0")
    implementation("org.jetbrains:kotlin-react-dom:17.0.2-pre.154-kotlin-1.5.0")
    implementation("org.jetbrains:kotlin-styled:5.2.3-pre.154-kotlin-1.5.0")

    implementation(npm("react-toolbox", "2.0.0-beta.13"))
    implementation(npm("react-youtube-lite", "1.0.1"))

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
        }
    }
}