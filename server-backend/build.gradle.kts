plugins {
    kotlin("jvm") // There are some bugs with Intellij older than 2021.1
    kotlin("plugin.serialization")
    id("com.github.gelangweilte-studenten.gradle-docker-tests") version "1.0.0"
    application
}

application.mainClass.set("de.hsaalen.cmt.ServerBackendKt")

val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)

kotlin.target {
    // Choose 'jvm' from disambiguating targets
    attributes.attribute(attr, "jvm")
    compilations.all {
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":common"))

    // Network framework
    implementation("io.ktor:ktor-server-core:1.5.4")
    implementation("io.ktor:ktor-server-cio:1.5.4") {
        because("Known issues with netty & jetty")
    }
    implementation("io.ktor:ktor-serialization:1.5.4")
    implementation("io.ktor:ktor-websockets:1.5.4")
    implementation("io.ktor:ktor-metrics-micrometer:1.5.4")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0") {
        because("To override deprecated version form ktor")
    }

    // Statistics & logging frameworks
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6") {
        // See https://github.com/MicroUtils/kotlin-logging
    }
    implementation("ch.qos.logback:logback-classic:1.2.3") {
        because("Ktor depends on this library and has issues when missing")
    }
    implementation("io.micrometer:micrometer-registry-prometheus:1.6.6")

    // JUnit test framework
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}