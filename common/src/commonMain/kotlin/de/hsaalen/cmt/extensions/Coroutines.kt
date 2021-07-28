package de.hsaalen.cmt.extensions

import de.hsaalen.cmt.events.ListenerBundle
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Extension function to simplify creation of a [CoroutineScope] based on the window dispatcher.
 */
expect val coroutines: CoroutineScope

/**
 * Launch a new coroutine that will be cancelled when unregistering listeners from [ListenerBundle].
 */
fun ListenerBundle.launch(coroutine: suspend () -> Unit) {
    val job = coroutines.launch { coroutine() }
    scopeElements += object : Closeable {
        override fun close() {
            job.cancel()
        }
    }
}
