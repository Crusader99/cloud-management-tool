package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.apiPathImport
import de.hsaalen.cmt.network.session.Client
import de.hsaalen.cmt.utils.ClientSupport
import io.ktor.client.request.forms.*
import io.ktor.http.*

// TODO: remove
internal interface RequestImport : ClientSupport {

    suspend fun import(fileName: String, fileContent: ByteArray): String {
        val url = Url("$apiEndpoint$apiPathImport")
        val parts = formData {
            val headersBuilder = HeadersBuilder()
            headersBuilder[HttpHeaders.ContentDisposition] = "filename=$fileName"
            headersBuilder[HttpHeaders.ContentType] = ContentType.Any.toString()
            headersBuilder[HttpHeaders.ContentLength] = fileContent.size.toString()

            append("file-1", headersBuilder.build()) {
                append(fileContent.decodeToString())
            }

        }
        return Client.request(url, json = false, timeout = 60_000) {
            method = HttpMethod.Post
            body = MultiPartFormDataContent(parts)
        }
    }

}
