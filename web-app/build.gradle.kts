plugins {
    kotlin("js")
}

dependencies {
    implementation(project(":common"))
    testImplementation(kotlin("test-js"))
}

kotlin.js {
    browser {
        distribution {
            directory = File("$buildDir/artifact-js/")
        }
    }
    binaries.executable()
}

// Create index.html file for testing
tasks.register("publish") {
    doLast {
        val outputFolder = File(buildDir, "artifact-js")
        val jsFile = File(outputFolder, project.name + ".js")
        val htmlFile = File(buildDir, "index.html")
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
