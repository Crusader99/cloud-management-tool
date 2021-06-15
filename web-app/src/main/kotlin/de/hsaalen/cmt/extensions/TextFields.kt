package de.hsaalen.cmt.extensions

import com.ccfraser.muirwik.components.MTextFieldProps
import org.w3c.dom.HTMLInputElement


/**
 * Extension helper function to simplify the text change event listener.
 */
fun MTextFieldProps.onTextChange(event: (String) -> Unit) {
    onChange = { e ->
        val input = e.target as? HTMLInputElement
        input?.value?.let { text ->
            event(text)
        }
    }
}
