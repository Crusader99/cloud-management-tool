import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies { // Use api instead implementation to allow transitive access from modules
                api("de.crusader:kotlin-extensions:1.0.17")
                api("de.crusader:library-objects:1.0.16")

                if (file("$rootDir/library-painter").exists()) {
                    // Allows easier debugging without publishing changes
                    api(project(":library-painter"))
                } else {
                    api("de.crusader:library-painter:1.0.27")
                }

                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
                implementation("io.ktor:ktor-client-core:1.5.4")
                implementation("io.ktor:ktor-client-serialization:1.5.4")
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
