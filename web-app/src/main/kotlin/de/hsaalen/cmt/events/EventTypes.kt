package de.hsaalen.cmt.events

import de.hsaalen.cmt.components.login.Credentials
import de.hsaalen.cmt.network.dto.client.ClientReferenceQueryDto
import de.hsaalen.cmt.network.dto.objects.Reference

/**
 * Registry for all possible custom events.
 */
enum class EventType {
    PRE_CREATE_NEW_DOCUMENT,
    PRE_DOCUMENT_IMPORT,
    PRE_FILE_UPLOAD,
    PRE_RECONNECT,
    PRE_LOGOUT,
    PRE_LOGIN,
    PRE_SWITCH_BACKEND,
    START_KEEP_ALIVE_JOB,
    PRE_USER_ADD_LABEL,
    PRE_USER_REMOVE_LABEL,
    PRE_USER_CLICK_ON_LABEL,
    PRE_USER_OPEN_REFERENCE,
    PRE_USER_DOWNLOAD_REFERENCE,
    PRE_USER_DELETE_REFERENCE,
    PRE_USER_RENAME_REFERENCE,
    PRE_USER_MODIFY_SEARCH,
}

/**
 * [Event] class for an event handler related to any [Reference] related action.
 */
data class ReferenceEvent(val reference: Reference) : Event

/**
 * [Event] class for an event handler related to a label modification.
 */
data class LabelEvent(val reference: Reference, val labelName: String) : Event

/**
 * Specific event type for the login event to allow passing parameters.
 */
data class LoginEvent(val credentials: Credentials, val isRegistration: Boolean) : Event

/**
 * Specific event type for the search event to allow searching for specific references.
 */
data class SearchEvent(val query: ClientReferenceQueryDto) : Event {
    constructor(searchName: String, filterLabels: Set<String>) : this(ClientReferenceQueryDto(searchName, filterLabels))
}

