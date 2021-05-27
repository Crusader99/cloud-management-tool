package de.hsaalen.cmt

/**
 * All react pages used in this web-app. The [isLoggedIn] property simplifies checks.
 */
enum class EnumPageType(val isLoggedIn: Boolean) {
    CONNECTING(false),
    UNAVAILABLE(false),
    AUTHENTICATION(false),
    OVERVIEW(true),
    EDIT_DOCUMENT(true),
}
