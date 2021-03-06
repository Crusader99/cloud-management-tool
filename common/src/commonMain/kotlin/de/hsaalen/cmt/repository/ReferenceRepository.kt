package de.hsaalen.cmt.repository

import de.hsaalen.cmt.network.dto.client.ClientCreateReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientDeleteReferenceDto
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.ContentType
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto

/**
 * Repository port for providing reference infrastructure. This can be implemented by the server for Mongo database
 * access or implemented for the client to access the server over network. The implementation can be injected using
 * dependency injection.
 */
interface ReferenceRepository {

    /**
     * Provide a list of all related references to search query.
     */
    suspend fun listReferences(query: ClientReferenceQueryDto = ClientReferenceQueryDto()): ServerReferenceListDto

    /**
     * Send request to repository for creating a new reference.
     */
    suspend fun createReference(request: ClientCreateReferenceDto): Reference

    /**
     * Request repository to create a new reference.
     */
    suspend fun createReferenceToDocument(
        displayName: String,
        textLines: String = "",
        labels: Set<String> = emptySet()
    ): Reference {
        val dto = ClientCreateReferenceDto(displayName, contentType = ContentType.TEXT, textLines.lines(), labels)
        return createReference(dto)
    }

    /**
     * Request repository to delete a reference by the reference object.
     */
    suspend fun deleteReference(reference: Reference) = deleteReference(reference.uuid)

    /**
     * Request repository to delete a reference by the given reference uuid.
     */
    suspend fun deleteReference(uuid: UUID) = deleteReference(ClientDeleteReferenceDto(uuid))

    /**
     * Send request repository to delete a reference.
     */
    suspend fun deleteReference(request: ClientDeleteReferenceDto)

    /**
     * Give a new title name to a reference.
     */
    suspend fun rename(uuid: UUID, newTitle: String)

}
