import java.time.Duration

plugins {
    kotlin("jvm") // There are some bugs with Intellij older than 2021.1
    kotlin("plugin.serialization")
    id("com.github.gelangweilte-studenten.gradle-docker-tests")
    //id("io.gitlab.arturbosch.detekt") // Code quality analyze tool TODO: enable when out-of-memory fixed
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
    implementation(project(":common"))

    // Network framework
    implementation("io.ktor:ktor-server-core:1.6.1")
    implementation("io.ktor:ktor-server-cio:1.6.1") {
        because("Known issues with netty & jetty")
    }
    implementation("io.ktor:ktor-serialization:1.6.1")
    implementation("io.ktor:ktor-websockets:1.6.1")
    implementation("io.ktor:ktor-metrics-micrometer:1.6.1")
    implementation("io.ktor:ktor-auth:1.6.1")
    implementation("io.ktor:ktor-auth-jwt:1.6.1")

    // Statistics & logging frameworks
    implementation("ch.qos.logback:logback-classic:1.2.3") {
        because("Ktor depends on this library and has issues when missing")
    }
    implementation("io.micrometer:micrometer-registry-prometheus:1.7.1")
    implementation("io.insert-koin:koin-logger-slf4j:3.1.2")

    // Use Koin as dependency injection framework
    implementation("io.insert-koin:koin-ktor:3.1.2")

    // JUnit test framework
    testImplementation(kotlin("test"))
    testImplementation("de.crusader:webscraper-selenium:3.1.0")
    testImplementation("de.crusader:webscraper-htmlunit:3.1.0")
}

tasks.test {
    useJUnitPlatform()
    timeout.set(Duration.ofSeconds(60L))
}

// Configure detekt code analyze tool to generate HTML report
//detekt {
//    ignoreFailures = true // Currently only print warning
//    reports {
//        html.enabled = true
//    }
//}
//
// The detekt analyze plugin caused out-of-memory in GitHub Actions.
// This is a workaround to disable detekt directly on build.
// (For mor information see https://github.com/detekt/detekt/issues/1894)
//tasks.getByName("check") {
//    this.setDependsOn(this.dependsOn.filterNot {
//        it is TaskProvider<*> && it.name == "detekt"
//    })
//}
