package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.apiPathListLabels
import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.websocket.LabelUpdateDto
import de.hsaalen.cmt.network.session.Client
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.repository.LabelRepository
import de.hsaalen.cmt.utils.JsonHelper
import io.ktor.http.*
import io.ktor.http.cio.websocket.*

/**
 * Provides access to server side label functionality.
 */
internal interface RequestLabel : Request, LabelRepository {

    /**
     * Add label to an existing reference by it's [UUID].
     */
    override suspend fun addLabel(reference: UUID, labelName: String) {
        val dto = LabelUpdateDto(reference, labelName, LabelChangeMode.ADD)
        val jsonText = JsonHelper.encode(dto)
        Session.instance?.webSocketSendingQueue?.send(Frame.Text(jsonText))
    }

    /**
     * Remove label from an existing reference by it's [UUID].
     */
    override suspend fun removeLabel(reference: UUID, labelName: String) {
        val dto = LabelUpdateDto(reference, labelName, LabelChangeMode.DELETE)
        val jsonText = JsonHelper.encode(dto)
        Session.instance?.webSocketSendingQueue?.send(Frame.Text(jsonText))
    }

    /**
     * List all labels from a user that are applied to any reference.
     */
    override suspend fun listLabels(): List<String> {
        val url = Url("$apiEndpoint$apiPathListLabels")
        return Client.request(url) {
            method = HttpMethod.Get
        }
    }

}
