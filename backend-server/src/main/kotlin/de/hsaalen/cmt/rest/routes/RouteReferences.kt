package de.hsaalen.cmt.rest.routes

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathCreateReference
import de.hsaalen.cmt.network.apiPathDeleteReference
import de.hsaalen.cmt.network.apiPathDownload
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.objects.UUID
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
 * Register and handle REST API routes from clients related to references and their content.
 */
fun Routing.routeReferences() = route("/" + RestPaths.base) {
    val repo: ReferenceRepository by inject()
    postWithSession(apiPathCreateReference) {
        val request: ClientCreateReferenceDto = call.receive()
        call.respond(repo.createReference(request))
    }
    postWithSession(apiPathDeleteReference) {
        val request: ClientDeleteReferenceDto = call.receive()
        call.respond(repo.deleteReference(request))
    }
    getWithSession("/upload") {
        call.respondText("Upload")
    }
    getWithSession("$apiPathDownload/{uuid}") {
        val uuid: String by call.parameters
        val docRepo: DocumentRepository by call.inject()
        val stream = docRepo.downloadContent(UUID(uuid)).byteInputStream()
        call.response.header(HttpHeaders.ContentDisposition, "attachment")
        call.respondOutputStream {
            stream.copyTo(this)
        }
    }
}
