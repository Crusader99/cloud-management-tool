package de.hsaalen.cmt.views.events

class MPKeyboardEvent(
    val keyCode: Int,
    val char: Char,
    val isControlDown: Boolean,
    val isShiftDown: Boolean
) {

    val isEnter
        get() = keyCode == 10 || keyCode == 13

}
