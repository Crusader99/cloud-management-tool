package de.hsaalen.cmt.repository

import de.hsaalen.cmt.network.dto.objects.UUID


/**
 * Repository port for providing label infrastructure. This can be implemented by the server using a SQL
 * database or implemented for the client to access the server over network. The implementation can be injected using
 * dependency injection.
 */
interface LabelRepository {

    /**
     * Add label to an existing reference by it's [UUID].
     */
    suspend fun addLabel(reference: UUID, labelName: String)

    /**
     * Remove label from an existing reference by it's [UUID].
     */
    suspend fun removeLabel(reference: UUID, labelName: String)

    /**
     * List all labels from a user that are applied to any reference.
     */
    suspend fun listLabels(): List<String>

}
