package de.hsaalen.cmt.rest

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathListLabels
import de.hsaalen.cmt.repository.LabelRepository
import de.hsaalen.cmt.session.getWithSession
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

/**
 * Register and handle REST API routes from clients related to labels.
 */
fun Routing.routeLabels() = route("/" + RestPaths.base) {
    // Lazy inject LabelRepository
    val repo: LabelRepository by inject()

    getWithSession(apiPathListLabels) { // Handle GET requests only when JWT cookie set and valid
        call.respond(repo.listLabels()) // Provide list of all used labels
    }
}
