package de.hsaalen.cmt.network.utils

import de.hsaalen.cmt.network.RestPaths

/**
 * Provide easy access to api endpoint.
 */
internal interface ClientSupport {

    /**
     * The url to use for requests to REST API server
     */
    val apiEndpoint
        get() = RestPaths.apiEndpoint

}
