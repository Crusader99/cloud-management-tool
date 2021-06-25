package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.apiPathListReferences
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.network.session.Client
import io.ktor.http.*

internal interface RequestListReferences : Request {

    /**
     * Provide a list of all related references to search query.
     */
    suspend fun listReferences(query: ClientReferenceQueryDto): ServerReferenceListDto {
        val url = Url("$apiEndpoint$apiPathListReferences")
        return Client.request(url) {
            method = HttpMethod.Post
            body = query
        }
    }

}
