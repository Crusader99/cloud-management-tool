import java.io.ByteArrayOutputStream

plugins {
    base // Basic plugin to provide clean tasks etc
    application // The application plugin configures the source sets for the IDE
}

/**
 * Cleanup previous builder files.
 */
val cleanupResources by tasks.registering {
    delete("$buildDir/electron")
}

/**
 * Copy compiled web-app to build folder.
 */
val copyResources by tasks.registering(Copy::class) {
    dependsOn(cleanupResources, ":web-app:build")
    from("../web-app/build/artifact-js")
    from("src/main/resources")
    into("$buildDir/electron")
}

/**
 * Build and execute docker image for building actual electron with related installers.
 */
val dockerElectronBuilder by tasks.registering {
    dependsOn(copyResources)
    doLast {
        val dockerImageId = ByteArrayOutputStream().apply {
            use { output ->
                exec {
                    workingDir(projectDir)
                    commandLine("docker", "build", "-q", ".")
                    standardOutput = output
                }
            }
        }.toString().trim()
        println("Built docker image: $dockerImageId")
        exec {
            workingDir(buildDir)
            commandLine("docker", "run", "-v", "./electron:/project:z", "-it", dockerImageId)
        }
    }
}

tasks.build {
    dependsOn(dockerElectronBuilder)
}
