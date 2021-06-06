package de.hsaalen.cmt.views.events

/**
 * Keyboard event on multi-platform code.
 *
 * Key codes are from http://javascriptkeycode.com/
 */
class MPKeyboardEvent(
    val keyCode: Int,
    val char: Char,
    val isControlDown: Boolean,
    val isShiftDown: Boolean
) {

    inline val isEnter
        get() = keyCode == 10 || keyCode == 13

    inline val isArrowLeft
        get() = keyCode == 37

    inline val isArrowUp
        get() = keyCode == 38

    inline val isArrowRight
        get() = keyCode == 39

    inline val isArrowDown
        get() = keyCode == 40

    inline val isDelete
        get() = keyCode == 46

    inline val isBackspace
        get() = keyCode == 8

}
