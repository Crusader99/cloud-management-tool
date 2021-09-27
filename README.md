# Cloud Management Tool

Online management tool for end2end encrypted files and documents.

---

## Features

* User data is client side encrypted using a private master key
* Changes are synchronized live between different devices / browser tabs of the same user
* Backend can be horizontally scaled to multiple container instances
* Common code is shared between server backend and web frontend

## Technical background

* [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) (share code between frontend and backend)
* [Ktor](https://ktor.io) (network framework for client & server)
* [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) (for JSON in REST API)
* [Insert-Koin](https://insert-koin.io) (dependency injection framework)
* [Exposed](https://github.com/JetBrains/Exposed) (ORM framework for SQL in Kotlin)
* [KMongo](https://litote.org/kmongo) (ORM framework for MongoDB in Kotlin)
* [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) (non-blocking programming)
* [Kotlin-Logging](https://github.com/MicroUtils/kotlin-logging) (multiplatform logging framework)
* [React](https://kotlinlang.org/docs/js-get-started.html)
  (for frontend of web-app, see [Kotlin/JS for React](https://kotlinlang.org/docs/js-get-started.html))
* [Material UI](https://material-ui.com) + [Muirwik](https://github.com/cfnz/muirwik) (provide react components)
* [Mockk](https://mockk.io) (Help mocking in test units)

## Databases

* [PostgreSQL](https://www.postgresql.org) for user management and references / labels
* [MongoDB](https://www.mongodb.com) for encrypted notes to allow updating only a single line
* [Minio](https://min.io) Amazon S3 compatible container as storage for encrypted files

## Build-tool configuration

* Gradle [multi-project](https://docs.gradle.org/current/userguide/intro_multi_project_builds.html) (all subprojects
  included in one repo)
* Automated JUnit-Tests with GitHub Actions
* Provide docker-compose file for backend setup
* Documentation with latex template and [plant-uml](https://plantuml.com/)

## Roadmap

* [x] Make backend technology decisions
* [x] Create running project for backend
* [x] Configure Dockerfile for backend
* [x] Configure Docker-Compose
* [x] Configure [ktor](https://ktor.io/) for backend
* [x] Make frontend GUI design decisions
* [x] Create running projects for frontend: android, web/pc
* [x] Experiment with compose and painter-library and [react](https://kotlinlang.org/docs/js-get-started.html)
* [x] Implement frontend login with react
* [x] Configure reverse proxy: [nginx](https://www.nginx.com/) or [traefik](https://traefik.io/)
* [x] Call REST-API from frontend on login event
* [x] Implement logout button in frontend
* [x] Decide which database to use for user management, file management.
* [x] REST-API server implementation for user registration & login
* [x] SQL DAO elements for user registration & login
* [x] Connect websocket to backend server
* [x] Remove JWT cookie on logout
* [x] Simplify debugging without complete rebuild
* [x] Ensure session restored after page reload
* [x] Make frontend work on Android
* [x] Build Electron app for PC
* [x] Provide hosted web-app online
* [x] Implement mongodb data structure for text documents
* [x] Implement algorithm for detecting changed lines in text
* [x] Implement live sync for document edit
* [x] Implement text editor in frontend
* [x] Allow offline access in Android app
* [x] Implement file upload / download
* [x] Encrypt files and documents
* [x] Configure Grafana & Prometheus
* [x] Provide statistics with Ktor: /metrics
* [x] Multi instance Redis support

## Build & execute project

To build the android project ensure the file `local.properties` exists in root project structure with following content.
Make sure the android sdk is installed and path is correct.

```
sdk.dir=/opt/android
```

Note when opening the full project with Android Studio, the `local.properties` file will be generated automatically.

### With gradle & docker-compose

Build all modules, including android:
> gradlew build

(Note: Java 16+ may cause build failures)

To skip the tests while Gradle build run:
> gradlew build -x test

Start backend using docker (opens reverse-proxy on port 80):
> sudo docker-compose up --build

Cleanup data and volumes:
> sudo docker-compose down -v

### Run frontend without docker-compose:

Debug web-app (opens dev-server on port 8081):
> gradle :web-app:browserDevelopmentRun --continuous

## Conventions

* Logic in code: [Keep it simple, stupid](https://en.wikipedia.org/wiki/KISS_principle)
* Write everything in english language
* Commit messages with convention from [Chris Beams](https://chris.beams.io/posts/git-commit/)
* [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
