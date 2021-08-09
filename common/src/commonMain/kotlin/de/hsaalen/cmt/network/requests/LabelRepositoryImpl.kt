package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.crypto.decrypt
import de.hsaalen.cmt.network.apiPathListLabels
import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.rsocket.LabelUpdateDto
import de.hsaalen.cmt.network.session.Client
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.repository.LabelRepository
import de.hsaalen.cmt.utils.ClientSupport
import de.hsaalen.cmt.utils.isValidLabelString
import io.ktor.http.*

/**
 * Provides access to server side label functionality.
 */
internal interface LabelRepositoryImpl : ClientSupport, LabelRepository {

    /**
     * Add label to an existing reference by its [UUID].
     */
    override suspend fun addLabel(reference: UUID, labelName: String) {
        if (!labelName.isValidLabelString()) {
            error("Label does not match expected format")
        }
        val dto = LabelUpdateDto(reference, labelName, LabelChangeMode.ADD)
        Session.instance?.sendLiveDTO(dto)
    }

    /**
     * Remove label from an existing reference by its [UUID].
     */
    override suspend fun removeLabel(reference: UUID, labelName: String) {
        val dto = LabelUpdateDto(reference, labelName, LabelChangeMode.REMOVE)
        Session.instance?.sendLiveDTO(dto)
    }

    /**
     * List all labels from a user that are applied to any reference.
     */
    override suspend fun listLabels(): List<String> {
        val url = Url("$apiEndpoint$apiPathListLabels")
        val encryptedLabels: List<String> = Client.request(url) {
            method = HttpMethod.Get
        }

        // Note that for labels the secureRandomizedPadding is disabled because encrypted
        // data has to be the same every time. This is required because of features like
        // the search by label function.
        return encryptedLabels.decrypt(secureRandomizedPadding = false)
    }

}
