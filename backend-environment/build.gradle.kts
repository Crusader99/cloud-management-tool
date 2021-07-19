plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") // Generate API documentation from source code
}

dependencies {
    implementation(project(":common"))
}

configurations.all {
    // Choose 'jvm' from disambiguating targets
    val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)
    attributes.attribute(attr, "jvm")
}
