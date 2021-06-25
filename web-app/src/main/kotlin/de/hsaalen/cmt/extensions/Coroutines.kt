package de.hsaalen.cmt.extensions

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher

/**
 * Extension function to simplify creation of a [CoroutineScope] based on the window dispatcher.
 */
val coroutines
    get() = CoroutineScope(window.asCoroutineDispatcher())