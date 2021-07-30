import java.time.Duration

plugins {
    kotlin("jvm") // There are some bugs with Intellij older than 2021.1
    kotlin("plugin.serialization")
    id("com.github.gelangweilte-studenten.gradle-docker-tests")
    id("org.jetbrains.dokka") // Generate API documentation from source code
    id("io.gitlab.arturbosch.detekt") // Code quality analyze tool
    application
}

application.mainClass.set("de.hsaalen.cmt.ServerBackendKt")

configurations.all {
    // Choose 'jvm' from disambiguating targets
    val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)
    attributes.attribute(attr, "jvm")
}

dependencies {
    implementation(project(":backend-database"))
    implementation(project(":backend-environment"))
    implementation(project(":common"))

    // Network framework
    implementation("io.ktor:ktor-server-core:1.6.2")
    implementation("io.ktor:ktor-server-cio:1.6.2") {
        because("Known issues with netty & jetty")
    }
    implementation("io.ktor:ktor-serialization:1.6.2")
    implementation("io.ktor:ktor-websockets:1.6.2")
    implementation("io.ktor:ktor-metrics-micrometer:1.6.2")
    implementation("io.ktor:ktor-auth:1.6.2")
    implementation("io.ktor:ktor-auth-jwt:1.6.2")

    // Use RSocket as better alternative to plain websockets (https://rsocket.io/)
    implementation("io.rsocket.kotlin:rsocket-transport-ktor-server:0.13.1")

    // Statistics & logging frameworks
    implementation("ch.qos.logback:logback-classic:1.2.3") {
        because("Ktor depends on this library and has issues when missing")
    }
    implementation("io.micrometer:micrometer-registry-prometheus:1.7.2")
    implementation("io.insert-koin:koin-logger-slf4j:3.1.2")

    // Use Koin as dependency injection framework
    implementation("io.insert-koin:koin-ktor:3.1.2")

    // JUnit test frameworks
    testImplementation(kotlin("test"))
    testImplementation("io.insert-koin:koin-test:3.1.2")
    testImplementation("io.mockk:mockk:1.12.0")
    testImplementation("io.ktor:ktor-server-test-host:1.6.2")
    testImplementation("de.crusader:webscraper-selenium:3.1.0")
    testImplementation("de.crusader:webscraper-htmlunit:3.1.0")
    testImplementation("io.ktor:ktor-client-core:1.6.2")
    testImplementation("io.ktor:ktor-client-cio:1.6.2")
    testImplementation("io.rsocket.kotlin:rsocket-transport-ktor-client:0.13.1")

}

// Exclude conflicting junit5 test dependency from ktor
configurations {
    testImplementation.get().exclude("org.jetbrains.kotlin", "kotlin-test-junit")
}

tasks.test {
    useJUnitPlatform()
    timeout.set(Duration.ofSeconds(60L))
}

// Configure detekt code analyze tool to generate HTML report
detekt {
    ignoreFailures = true // Currently only print warning
    reports {
        html.enabled = true
    }
}
