plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") // Generate API documentation from source code
    id("io.gitlab.arturbosch.detekt") // Code quality analyze tool
}

dependencies {
    implementation(project(":common"))

    // Required for EventModuleSerializer to registering event instances in serialization module
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.0")

    // Reflection libraries to allow scanning for event instances
    implementation("org.reflections:reflections:0.9.12")
    implementation(kotlin("reflect"))
}

configurations.all {
    // Choose 'jvm' from disambiguating targets
    val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)
    attributes.attribute(attr, "jvm")
}

// Configure detekt code analyze tool to generate HTML report
detekt {
    ignoreFailures = true // Only print warning
    reports {
        html.enabled = true
    }
}
