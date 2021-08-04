package de.hsaalen.cmt.rest.routes

import de.hsaalen.cmt.network.*
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateRenameDto
import de.hsaalen.cmt.repository.DocumentRepository
import de.hsaalen.cmt.repository.ReferenceRepository
import de.hsaalen.cmt.session.getWithSession
import de.hsaalen.cmt.session.postWithSession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.koin.ktor.ext.inject

/**
 * Register and handle REST API routes from clients that are related to any reference.
 */
fun Routing.routeReferences() = route("/" + RestPaths.base) {
    val repo: ReferenceRepository by inject()
    getWithSession(apiPathListReferences) {
        val request = ClientReferenceQueryDto()
        call.respond(repo.listReferences(request))
    }
    postWithSession(apiPathListReferences) {
        val request: ClientReferenceQueryDto = call.receive()
        call.respond(repo.listReferences(request))
    }
    postWithSession(apiPathCreateReference) {
        val request: ClientCreateReferenceDto = call.receive()
        call.respond(repo.createReference(request))
    }
    postWithSession(apiPathDeleteReference) {
        val request: ClientDeleteReferenceDto = call.receive()
        call.respond(repo.deleteReference(request))
    }
    postWithSession(apiPathRenameReference) {
        val request: ReferenceUpdateRenameDto = call.receive()
        call.respond(repo.rename(request.uuid, request.newName))
    }
}
