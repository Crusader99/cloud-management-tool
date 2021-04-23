package de.hsaalen.cmt.rest

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.serialization.json.Json
import org.slf4j.event.Level

object RestServer {

    /**
     * Configure an embedded HTTP server for providing a REST API.
     */
    fun start(port: Int) = embeddedServer(CIO, port) {
        install(CallLogging) {
            // Configure default logging level
            level = Level.INFO
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        registerRoutes()
    }

}