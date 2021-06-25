package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.apiPathDeleteReference
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.session.Client
import io.ktor.http.*

internal interface RequestDeleteReference : Request {

    /**
     * Request server to delete a reference.
     */
    suspend fun deleteReference(reference: Reference) {
        val url = Url("$apiEndpoint$apiPathDeleteReference")
        return Client.request(url) {
            method = HttpMethod.Post
            body = ClientDeleteReferenceDto(reference.uuid)
        }
    }

}
