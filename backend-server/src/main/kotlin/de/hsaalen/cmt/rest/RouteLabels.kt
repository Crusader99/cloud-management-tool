package de.hsaalen.cmt.rest

import de.hsaalen.cmt.jwt.readJwtCookie
import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathListLabels
import de.hsaalen.cmt.repository.LabelRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

/**
 * Register and handle REST API routes from clients related to labels.
 */
fun Routing.routeLabels() = route("/" + RestPaths.base) {
    authenticate {
        get(apiPathListLabels) { // Provide list of all used labels
            val userEmail = call.request.readJwtCookie().email
            val repo: LabelRepository by call.inject { parametersOf(userEmail) }
            call.respond(repo.listLabels())
        }
    }
}
