plugins {
    kotlin("jvm") // There are some bugs with Intellij older than 2021.1
    kotlin("plugin.serialization")
    application
}

application.mainClass.set("de.hsaalen.cmt.MainKt")

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
    implementation("io.ktor:ktor-server-core:1.5.3")
    implementation("io.ktor:ktor-server-cio:1.5.3") {
        because("Known issues with netty & jetty")
    }

    // Logging framework
    implementation("ch.qos.logback:logback-classic:1.2.3")

    // JUnit test framework
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
