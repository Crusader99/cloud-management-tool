package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.apiPathCreateReference
import de.hsaalen.cmt.network.apiPathDeleteReference
import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.rsocket.ReferenceUpdateAddDto
import de.hsaalen.cmt.network.session.Client
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.repository.ReferenceRepository
import de.hsaalen.cmt.utils.ClientSupport
import de.hsaalen.cmt.utils.decodeProtobufData
import io.ktor.http.*
import io.rsocket.kotlin.payload.Payload
import io.rsocket.kotlin.payload.buildPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository port for providing reference infrastructure. Implemented for the client to access the server over network.
 * The implementation can be injected using dependency injection.
 */
internal interface ReferenceRepositoryImpl : ClientSupport, ReferenceRepository {

    /**
     * Provide a list of all related references to search query.
     */
    override suspend fun listReferences(query: ClientReferenceQueryDto): Flow<Reference> {
        val rSocket = Session.instance?.rSocket ?: error("Not in session")
        return rSocket.requestStream(Payload.Empty)
            .map { it.decodeProtobufData<ReferenceUpdateAddDto>().decrypt().reference }
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

}
