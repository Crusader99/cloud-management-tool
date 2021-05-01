package de.hsaalen.cmt.views.api

import de.hsaalen.cmt.views.events.MPKeyboardEvent
import de.hsaalen.cmt.views.events.MPMouseEvent
import de.hsaalen.cmt.views.events.MPWheelEvent
import de.crusader.objects.Rectangle
import de.crusader.painter.Painter

abstract class MPView {

    var rectangle = Rectangle()

    var cursor = MPCursor.DEFAULT

    var x: Int
        get() = rectangle.x
        set(value) {
            rectangle = rectangle.x(value)
        }

    var y: Int
        get() = rectangle.y
        set(value) {
            rectangle = rectangle.y(value)
        }

    var width: Int
        get() = rectangle.width
        set(value) {
            rectangle = rectangle.width(value)
        }

    var height: Int
        get() = rectangle.height
        set(value) {
            rectangle = rectangle.height(value)
        }

    open fun onRepaint(p: Painter){}

    open fun onMouseMove(e: MPMouseEvent) {}

    open fun onMouseDown(e: MPMouseEvent) {}

    open fun onMouseUp(e: MPMouseEvent) {}

    open fun onMouseScroll(e: MPWheelEvent) {}

    open fun onMouseExit() {}

    open fun onKeyDown(e: MPKeyboardEvent) {}

    open fun onKeyUp(e: MPKeyboardEvent) {}

    open fun onThemeChanged() {}

}
