package de.hsaalen.cmt.extensions

import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Extension function to simplify creation of a [CoroutineScope] based on the window dispatcher.
 */
actual val coroutines
    get() = CoroutineScope(EmptyCoroutineContext)
