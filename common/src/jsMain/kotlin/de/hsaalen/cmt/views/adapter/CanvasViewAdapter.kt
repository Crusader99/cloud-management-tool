package de.hsaalen.cmt.views.adapter

import de.crusader.objects.Point
import de.crusader.objects.position
import de.crusader.painter.draw
import de.hsaalen.cmt.views.components.TestView
import de.hsaalen.cmt.views.api.MPView
import de.hsaalen.cmt.views.events.MPKeyboardEvent
import de.hsaalen.cmt.views.events.MPMouseButton
import de.hsaalen.cmt.views.events.MPMouseEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent

/**
 * This class is an experimental adapter between canvas views and own abstract multiplatform views
 */
class CanvasViewAdapter(private val canvas: HTMLCanvasElement) {

    private val view: MPView = TestView()
    private var previousMouse: Point? = null

    init {
        canvas.onmousemove = { view.onMouseMove(it.toMultiPlatform()) }
        canvas.onmousedown = { view.onMouseDown(it.toMultiPlatform()) }
        canvas.onmouseup = { view.onMouseUp(it.toMultiPlatform()) }

        GlobalScope.launch {
            while (isActive) {
                delay(200)
                canvas.draw { p ->
                    view.onRepaint(p)
                }
            }
        }
    }

    private fun KeyboardEvent.toMultiPlatform() =
        MPKeyboardEvent(
            this.keyCode,
            this.charCode.toChar(),
            isControlDown = false,
            isShiftDown = false
        )

    private fun MouseEvent.toMultiPlatform() =
        MPMouseEvent(this.position, this.position, MPMouseButton.LEFT, 1)

}
