# se-project

Online management tool for files and documents

## Conventions:

* Logic in code: [Keep it simple, stupid](https://en.wikipedia.org/wiki/KISS_principle)
* Write everything in english language
* Commit messages with convention from [Chris Beams](https://chris.beams.io/posts/git-commit/)
* [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)

## Build-tool configuration:

* Gradle [multi-project](https://docs.gradle.org/current/userguide/intro_multi_project_builds.html) (all subprojects
  included in one repo)
* Automated JUnit-Tests with GitHub Actions
* Provide docker-compose file for backend setup
* Documentation with latex template and [plant-uml](https://plantuml.com/)

## Technical background:

* [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) (share code between clients and backend)
* [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines) (lightweight multithreading)
* [Ktor](https://ktor.io/) (for client & server as network framework)
* [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) (for JSON in REST API)
* [React](https://kotlinlang.org/docs/js-get-started.html) (as frontend library)
* [Material UI](https://material-ui.com/) (provides react components)

## Platforms:

* Browser: React-App
* Smartphone: Android native app or React-Native

## Roadmap:

* [x] Make backend technology decisions
* [x] Create running project for backend
* [x] Configure Dockerfile for backend
* [x] Configure Docker-Compose
* [x] Configure [Ktor](https://ktor.io/) for backend
* [x] Make frontend GUI design decisions
* [x] Create running projects for frontend: android, web/pc
* [x] Experiment with compose and painter-library and react
* [x] Implement frontend login app with react
* [x] Configure reverse proxy: [nginx](https://www.nginx.com/) or [traefik](https://traefik.io/)
* [x] Call REST-API from frontend on login event
* [x] Implement logout button in frontend
* [ ] Decide which database to use for user management, file management, notes.
* [ ] Basic server implementation for user registration & login
* [ ] Implement text editor in frontend
* [ ] Connect websocket with server
* [ ] Implement live sync for noted
* [ ] ...
* [x] Configure Grafana & Prometheus
* [x] Provide statistics with Ktor: /metrics
* [ ] ...
* [ ] Select all repository link references for documentation
* [ ] Submit latex documentation until 15.08.2021

## Build & execute project

### With gradle & docker-compose:

Build all modules, including android:
> gradlew build

Start backend using docker:
> sudo docker-compose up --build

Cleanup data and volumes:
> sudo docker-compose down -v

### Execute without docker-compose:

Debug web-app:
> gradle :web-app:browserDevelopmentRun --continuous

Run server-backend:
> gradle :server-backend:run
