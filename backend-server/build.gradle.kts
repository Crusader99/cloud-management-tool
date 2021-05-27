plugins {
    kotlin("jvm") // There are some bugs with Intellij older than 2021.1
    kotlin("plugin.serialization")
    id("com.github.gelangweilte-studenten.gradle-docker-tests") version "1.1.1"
    application
}

application.mainClass.set("de.hsaalen.cmt.ServerBackendKt")

kotlin.target {
    compilations.all {
        kotlinOptions.jvmTarget = "1.8"
    }
}

configurations.all {
    // Choose 'jvm' from disambiguating targets
    val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)
    attributes.attribute(attr, "jvm")
}

dependencies {
    implementation(project(":backend-database"))
    implementation(project(":common"))

    // Network framework
    implementation("io.ktor:ktor-server-core:1.5.4")
    implementation("io.ktor:ktor-server-cio:1.5.4") {
        because("Known issues with netty & jetty")
    }
    implementation("io.ktor:ktor-serialization:1.5.4")
    implementation("io.ktor:ktor-websockets:1.5.4")
    implementation("io.ktor:ktor-metrics-micrometer:1.5.4")
    implementation("io.ktor:ktor-auth:1.5.4")
    implementation("io.ktor:ktor-auth-jwt:1.5.4")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.0") {
        because("To override deprecated version form ktor")
    }

    // Statistics & logging frameworks
    // See https://github.com/MicroUtils/kotlin-logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.2.3") {
        because("Ktor depends on this library and has issues when missing")
    }
    implementation("io.micrometer:micrometer-registry-prometheus:1.7.0")

    // JUnit test framework
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
