import java.io.ByteArrayOutputStream

plugins {
    base
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
    into("$buildDir/electron")
}

/**
 * Prepare the package.json file, required for npm/yarn.
 */
val prepareFiles by tasks.registering {
    dependsOn(copyResources)
    doLast {
        val packageJson = """{
  "name": "electron",
  "version": "1.0.0",
  "description": "",
  "main": "web-app.js",
  "author": "Simon Forschner",
  "license": "MIT"
}"""
        file("$buildDir/electron/package.json").writeText(packageJson)
    }
}

/**
 * Build and execute docker image for building actual electron with related installers.
 */
val dockerElectronBuilder by tasks.registering {
    dependsOn(prepareFiles)
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
