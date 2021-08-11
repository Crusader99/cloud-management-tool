package de.hsaalen.cmt.repository

import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.objects.UUID


/**
 * Repository port for providing label infrastructure. This can be implemented by the server using a SQL
 * database or implemented for the client to access the server over network. The implementation can be injected using
 * dependency injection.
 */
interface LabelRepository {

    /**
     * Add label to an existing reference by its [UUID].
     */
    suspend fun addLabel(reference: UUID, labelName: String)

    /**
     * Add label to an existing reference by its [Reference] instance.
     */
    suspend fun addLabel(reference: Reference, labelName: String) {
        if (labelName in reference.labels) {
            error("Label already added")
        }
        addLabel(reference.uuid, labelName)
    }

    /**
     * Remove label from an existing reference by its [UUID].
     */
    suspend fun removeLabel(reference: UUID, labelName: String)

    /**
     * Remove label from an existing reference by its [Reference] instance.
     */
    suspend fun removeLabel(reference: Reference, labelName: String) {
        if (labelName in reference.labels) {
            // Only remove label when label existing
            removeLabel(reference.uuid, labelName)
        }
    }

    /**
     * List all labels from a user that are applied to any reference.
     */
    suspend fun listLabels(): Set<String>

}
