package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.apiPathCreateReference
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.session.Client
import io.ktor.http.*

internal interface RequestCreateReference : Request {

    /**
     * Request server to create a new reference.
     */
    suspend fun createReference(displayName: String, documentContent: String = "") {
        createReference(ClientCreateReferenceDto(displayName, content = documentContent))
    }

    /**
     * Request server to create a new reference.
     */
    suspend fun createReference(dto: ClientCreateReferenceDto) {
        val url = Url("$apiEndpoint$apiPathCreateReference")
        return Client.request(url) {
            method = HttpMethod.Post
            body = dto
        }
    }

}
