package de.hsaalen.cmt.network

/**
 * Object keeping paths to REST API targets.
 */
object RestPaths {

    /**
     * The base path for REST API.
     */
    const val base = "api/v1.0"

    /**
     * The url to use for requests to REST API server.
     */
    var apiEndpoint = base

}

const val apiPathDeleteReference = "/deleteReference"

const val apiPathRenameReference = "/renameReference"

const val apiPathCreateReference = "/createReference"

const val apiPathListReferences = "/listReferences"

const val apiPathListLabels = "/listLabels"

const val apiPathImport = "/import"

const val apiPathUploadFile = "/file/upload"

const val apiPathDownloadFile = "/file/download"

const val apiPathDownloadDocument = "/document/download"

const val apiPathAuthRestore = "/restore"

const val apiPathAuthLogin = "/login"

const val apiPathAuthLogout = "/logout"

const val apiPathAuthRegister = "/register"

const val apiPathRSocket = "/rsocket"
