package de.hsaalen.cmt.rest.routes

import de.hsaalen.cmt.network.RestPaths
import de.hsaalen.cmt.network.apiPathDownloadDocument
import de.hsaalen.cmt.network.apiPathDownloadFile
import de.hsaalen.cmt.network.apiPathUploadFile
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.repository.DocumentRepository
import de.hsaalen.cmt.repository.FileRepository
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
 * Register and handle REST API routes from clients that are related to file and their content.
 */
fun Routing.routeFiles() = route("/" + RestPaths.base) {
    val repo: FileRepository by inject()
    postWithSession("$apiPathUploadFile/{uuid}") {
        val uuid: String by call.parameters
        repo.upload(UUID(uuid), call.receiveChannel().toByteArray())
        call.respond(Unit)
    }
    getWithSession("$apiPathDownloadFile/{uuid}") {
        val uuid: String by call.parameters
        call.respondBytes(repo.download(UUID(uuid)), ContentType.Application.OctetStream)
    }
    getWithSession("$apiPathDownloadDocument/{uuid}") {
        val uuid: String by call.parameters
        val docRepo: DocumentRepository by call.inject()
        val stream = docRepo.downloadDocument(UUID(uuid)).byteInputStream()
        call.response.header(HttpHeaders.ContentDisposition, "attachment")
        call.respondOutputStream {
            stream.copyTo(this)
        }
    }
}
