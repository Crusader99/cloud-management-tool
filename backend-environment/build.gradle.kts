plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") // Generate API documentation from source code
    id("io.gitlab.arturbosch.detekt") // Code quality analyze tool
}

dependencies {
    implementation(project(":common"))
}

configurations.all {
    // Choose 'jvm' from disambiguating targets
    val attr = Attribute.of("de.crusader.targetAttribute", String::class.java)
    attributes.attribute(attr, "jvm")
}

// Configure detekt code analyze tool to generate HTML report
detekt {
    ignoreFailures = true // Currently only print warning
    reports {
        html.enabled = true
    }
}
