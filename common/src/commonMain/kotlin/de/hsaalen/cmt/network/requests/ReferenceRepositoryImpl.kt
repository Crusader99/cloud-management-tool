package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.network.apiPathCreateReference
import de.hsaalen.cmt.network.apiPathDeleteReference
import de.hsaalen.cmt.network.apiPathListReferences
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.network.session.Client
import de.hsaalen.cmt.network.utils.ClientSupport
import de.hsaalen.cmt.repository.ReferenceRepository
import io.ktor.http.*

/**
 * Repository port for providing reference infrastructure. Implemented for the client to access the server over network.
 * The implementation can be injected using dependency injection.
 */
internal interface ReferenceRepositoryImpl : ClientSupport, ReferenceRepository {

    /**
     * Provide a list of all related references to search query.
     */
    override suspend fun listReferences(query: ClientReferenceQueryDto): ServerReferenceListDto {
        val url = Url("$apiEndpoint$apiPathListReferences")
        val dto: ServerReferenceListDto = Client.request(url) {
            method = HttpMethod.Post
            body = query
        }
        return dto.decrypt()
    }

    /**
     * Request server to create a new reference.
     */
    override suspend fun createReference(request: ClientCreateReferenceDto): Reference {
        val url = Url("$apiEndpoint$apiPathCreateReference")
        val reference: Reference = Client.request(url) {
            method = HttpMethod.Post
            body = request.encrypt()
        }
        return reference.decrypt()
    }

    /**
     * Request server to delete a reference.
     */
    override suspend fun deleteReference(request: ClientDeleteReferenceDto) {
        val url = Url("$apiEndpoint$apiPathDeleteReference")
        return Client.request(url) {
            method = HttpMethod.Post
            body = request
        }
    }

    /**
     * Download the content of a specific reference by uuid.
     */
    override suspend fun downloadContent(uuid: UUID): String {
        val url = Url("$apiEndpoint/download/$uuid")
        val encryptedText: String = Client.request(url) {
            method = HttpMethod.Get
        }

        // Get key and decrypt line by line
        return encryptedText.lineSequence().decrypt().joinToString("\n")
    }

}
