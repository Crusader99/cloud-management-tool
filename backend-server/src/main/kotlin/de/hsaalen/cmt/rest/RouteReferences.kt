package de.hsaalen.cmt.rest

import de.hsaalen.cmt.jwt.readJwtCookie
import de.hsaalen.cmt.network.*
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.repository.ReferencesRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import org.koin.core.parameter.parametersOf
import org.koin.ktor.ext.inject

/**
 * Register and handle REST API routes from clients related to references and their content.
 */
fun Routing.routeReferences() = route("/" + RestPaths.base) {
    authenticate {
        get(apiPathListReferences) {
            val request = ClientReferenceQueryDto()
            call.respond(call.injectRepository().listReferences(request))
        }
        post(apiPathListReferences) {
            val request: ClientReferenceQueryDto = call.receive()
            call.respond(call.injectRepository().listReferences(request))
        }
        post(apiPathCreateReference) {
            val request: ClientCreateReferenceDto = call.receive()
            call.respond(call.injectRepository().createReference(request))
        }
        post(apiPathDeleteReference) {
            val request: ClientDeleteReferenceDto = call.receive()
            call.respond(call.injectRepository().deleteReference(request))
        }
        get("/upload") {
            call.respondText("Upload")
        }
        get("/download/{uuid}") {
            val uuid: String by call.parameters
            val stream = call.injectRepository().downloadContent(uuid).byteInputStream()
            call.response.header(HttpHeaders.ContentDisposition, "attachment")
            call.respondOutputStream {
                stream.copyTo(this)
            }
        }
        post(apiPathImport) { // TODO: update or remove
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
}

/**
 * Inject [ReferencesRepository] by reading the email from the JWT payload.
 */
private fun ApplicationCall.injectRepository(): ReferencesRepository {
    val userEmail = request.readJwtCookie().email
    val repository: ReferencesRepository by inject { parametersOf(userEmail) }
    return repository // Repository is created for specific user using dependency injection
}
