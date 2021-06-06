package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.session.Client
import io.ktor.http.*

internal interface RequestDownload : Request {

    suspend fun download(uuid: String): String {
        val url = Url("$apiEndpoint/download/$uuid")
        return Client.request(url) {
            method = HttpMethod.Get
        }
    }

}
