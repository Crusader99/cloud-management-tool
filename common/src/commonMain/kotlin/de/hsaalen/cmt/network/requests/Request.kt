package de.hsaalen.cmt.network.requests

import de.hsaalen.cmt.network.RestPaths

internal interface Request {

    /**
     * The url to use for requests to REST API server
     */
    val apiEndpoint
        get() = RestPaths.apiEndpoint

}
