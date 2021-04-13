plugins {
    kotlin("js")
}

dependencies {
    implementation(project(":common"))

    // React components
    implementation("org.jetbrains:kotlin-react:17.0.1-pre.148-kotlin-1.4.30")
    implementation("org.jetbrains:kotlin-react-dom:17.0.1-pre.148-kotlin-1.4.30")
    implementation("org.jetbrains:kotlin-styled:5.2.1-pre.148-kotlin-1.4.30")

    // Test framework on javascript platform
    testImplementation(kotlin("test-js"))
}

kotlin {
    js {
        binaries.executable()
        browser {
            // For continuous integration: gradle browserDevelopmentRun --continuous
            distribution {
                directory = File("$buildDir/artifact-js/")
            }
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
}

// Create index.html file for testing
val publish by tasks.registering {
    doLast {
        val outputFolder = File(buildDir, "artifact-js")
        val jsFile = File(outputFolder, project.name + ".js")
        val htmlFile = File(buildDir, "index.html")
        if (htmlFile.exists()) {
            return@doLast // Skip
        }
        val text = StringBuilder()
        text.append("<!DOCTYPE HTML><html lang=\"de\">")
        text.append("<head>")
        text.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>")
        text.append("</head>")
        text.append("<body>")
        text.append("<script type=\"text/javascript\" src=\"${outputFolder.name}/${jsFile.name}\"></script>")
        text.append("</body>")
        text.append("</html>")
        htmlFile.writeText(text.toString(), Charsets.UTF_8)
        println("Created html file at $htmlFile")
    }
}

tasks.build {
    finalizedBy(publish)
}