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

/**
 * Extension helper function to simplify handling enter key down.
 */
fun MTextFieldProps.onEnterKey(event: () -> Unit) {
    val superEvent = onKeyPress
    onKeyPress = { e ->
        superEvent?.invoke(e)
        if (e.key == "Enter") {
            event.invoke()
        }
    }
}
