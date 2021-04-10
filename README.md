# se-project

Online management tool for files and documents

Conventions:
* Logic in code: [Keep it simple, stupid](https://en.wikipedia.org/wiki/KISS_principle)
* Write everything in english language
* Commit messages with convention from [Chris Beams](https://chris.beams.io/posts/git-commit/)
* [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)

Build-tool configuration:
* Gradle [multi-project](https://docs.gradle.org/current/userguide/intro_multi_project_builds.html) (all subprojects included in one repo)
* Automated JUnit-Tests with GitHub Actions
* Provide docker-compose file for backend setup
* Documentation with latex template and [plant-uml](https://plantuml.com/)

Technical background:
* [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) (Share code between clients and backend)
* [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) (lightweight multithreading)
* [Ktor](https://ktor.io/) for client & server (as network framework)
* [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) (for JSON in REST API)

Platforms:
* Android
* Browser / Java-App?

Frontend possibilities:
* Always native way, multiplatform painter-library for some components
* [compose-jb](https://github.com/JetBrains/compose-jb) (currently in alpha)
* [react native](https://kotlinlang.org/docs/js-get-started.html) for all? (no experience)

Roadmap:
* [x] Make backend technology decisions
* [x] Create running project for backend
* [x] Configure Dockerfile for backend
* [x] Configure Docker-Compose
* [ ] Configure [Ktor](https://ktor.io/) for backend
* [ ] Make frontend GUI design decisions
* [ ] Create running projects for frontend: android, web/pc
* [ ] Experiment with compose and painter-library and react
* [ ] ...
* [ ] Select all repository link references for documentation
