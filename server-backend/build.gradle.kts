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

    // JUnit test framework
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
