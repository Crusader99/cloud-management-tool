package de.hsaalen.cmt.rest

import de.hsaalen.cmt.jwt.readJwtCookie
import de.hsaalen.cmt.network.*
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.repositories.ReferencesRepositoryImpl
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*

/**
 * Register and handle REST API routes from clients related to references and their content.
 */
fun Routing.routeReferences() = route("/" + RestPaths.base) {
    authenticate {
        get(apiPathListReferences) {
            val query = ClientReferenceQueryDto()
            val result = ReferencesRepositoryImpl.listReferences(query)
            call.respond(result)
        }
        post(apiPathListReferences) {
            val query: ClientReferenceQueryDto = call.receive()
            val result = ReferencesRepositoryImpl.listReferences(query)
            call.respond(result)
        }
        get(apiPathListReferences) {
            throw IllegalArgumentException("Expected POST request!")
        }
        post(apiPathCreateReference) {
            val info: ClientCreateReferenceDto = call.receive()
            val email = call.request.readJwtCookie().email
            val result = ReferencesRepositoryImpl.createItem(info, email)
            call.respond(result)
        }
        post(apiPathDeleteReference) {
            val info: ClientDeleteReferenceDto = call.receive()
            // TODO: check for access permissions to this file
            val result = ReferencesRepositoryImpl.deleteReferences(info.uuid)
            call.respond(result)
        }
        get("/upload") {
            call.respondText("Upload")
        }
        get("/download/{uuid}") {
            val uuid: String by call.parameters
            val stream = ReferencesRepositoryImpl.downloadContent(uuid)
            call.response.header(HttpHeaders.ContentDisposition, "attachment")
            call.respondOutputStream {
                stream.copyTo(this)
            }
        }
        post(apiPathImport) { // TODO: update or remove
            val multipart = call.receiveMultipart()
            val email = call.request.readJwtCookie().email
            multipart.forEachPart { part ->
                // Get all file parts of this multipart
                if (part is PartData.FileItem) {
                    val fileName = part.originalFileName ?: "unknown"
                    val fileContent = part.streamProvider()
//                        ServiceReferences.import(fileName, fileContent, email)
                }
                // Close part to prevent memory leaks
                part.dispose()
            }
            call.respondText("Imported")
        }
    }
}
