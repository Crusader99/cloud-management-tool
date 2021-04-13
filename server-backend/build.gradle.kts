plugins {
    kotlin("jvm") // There are some bugs with Intellij older than 2021.1
    kotlin("plugin.serialization")
    application
}

application.mainClass.set("de.hsaalen.cmt.ServerBackendKt")

val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)

kotlin.target {
    // Choose 'jvm' from disambiguating targets
    attributes.attribute(attr, "jvm")
    compilations.all {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.languageVersion = "1.5"
    }
}

dependencies {
    implementation(project(":common"))

    // Network framework
    implementation("io.ktor:ktor-server-core:1.5.3")
    implementation("io.ktor:ktor-server-cio:1.5.3") {
        because("Known issues with netty & jetty")
    }

    // Statistics & logging frameworks
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.6") {
        // See https://github.com/MicroUtils/kotlin-logging
    }
    implementation("ch.qos.logback:logback-classic:1.2.3") {
        because("Ktor depends on this library and has issues when missing")
    }
    implementation("org.influxdb:influxdb-java:2.21") {
        because("Writing statistics to influxdb for grafana")
    }

    // JUnit test framework
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
