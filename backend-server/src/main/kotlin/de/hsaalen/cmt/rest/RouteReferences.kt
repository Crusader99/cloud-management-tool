package de.hsaalen.cmt.rest

import de.hsaalen.cmt.network.*
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.repository.ReferenceRepository
import de.hsaalen.cmt.session.getWithSession
import de.hsaalen.cmt.session.postWithSession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
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
    getWithSession("/upload") {
        call.respondText("Upload")
    }
    getWithSession("$apiPathDownload/{uuid}") {
        val uuid: String by call.parameters
        val stream = repo.downloadContent(UUID(uuid)).byteInputStream()
        call.response.header(HttpHeaders.ContentDisposition, "attachment")
        call.respondOutputStream {
            stream.copyTo(this)
        }
    }
    postWithSession(apiPathImport) { // TODO: update or remove
        val multipart = call.receiveMultipart()
        multipart.forEachPart { part ->
            // Get all file parts of this multipart
            if (part is PartData.FileItem) {
                val fileName = part.originalFileName ?: "unknown"
                val fileContent = part.streamProvider()
            }
            // Close part to prevent memory leaks
            part.dispose()
        }
        call.respondText("Imported")
    }
}
