plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
}

val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)

kotlin {
    android {
        attributes.attribute(attr, "android")
    }
    jvm {
        attributes.attribute(attr, "jvm")
    }
    js(BOTH) {
        useCommonJs()
        browser {
            testTask {
                useKarma {
                    useFirefox()
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies { // Use api instead implementation to allow transitive access from modules
                // Statistics & logging frameworks
                // See https://github.com/MicroUtils/kotlin-logging
                implementation("io.github.microutils:kotlin-logging:2.0.8")

                api("de.crusader:kotlin-extensions:1.1.1")
                api("de.crusader:library-objects:1.1.1")

                if (file("$rootDir/library-painter").exists()) {
                    // Allows easier debugging without publishing changes
                    api(project(":library-painter"))
                } else {
                    api("de.crusader:library-painter:1.1.1")
                }

                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
                implementation("io.ktor:ktor-client-core:1.6.0")
                implementation("io.ktor:ktor-client-serialization:1.6.0")
                implementation("io.ktor:ktor-client-websockets:1.6.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val androidMain by getting {
            dependencies {
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = rootProject.version.toString()
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
