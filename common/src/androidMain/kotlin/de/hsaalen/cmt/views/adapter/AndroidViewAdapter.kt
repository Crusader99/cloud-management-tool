package de.hsaalen.cmt.views.adapter

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import de.crusader.extensions.toFullString
import de.crusader.objects.Point
import de.crusader.objects.color.Color
import de.crusader.painter.impl.AndroidCanvasPainter
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.views.api.MPView
import de.hsaalen.cmt.views.components.TestView
import de.hsaalen.cmt.views.events.MPKeyboardEvent
import de.hsaalen.cmt.views.events.MPMouseButton
import de.hsaalen.cmt.views.events.MPMouseEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun Throwable.report() = debug(toFullString())

fun debug(msg: String) = Log.d("DEBUG", msg)

/**
 * This class is an experimental adapter between android views and own abstract multiplatform views
 */
class AndroidViewAdapter
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val view: MPView = TestView()
    private var previousMouse: Point? = null

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        coroutines.launch {
            while (isActive) {
                delay(60) // Reduce delay to get higher fps
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            val p = AndroidCanvasPainter(canvas, 0, 0, width, height)
            p.createRectangle()
                .color(Color.BLACK)
                .size(p.size)
                .filled(true)
                .draw()

            view.onRepaint(p)
        } catch (ex: Exception) {
            ex.report()
        } finally {
            super.onDraw(canvas)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        try {
            val e = MPKeyboardEvent(event.keyCode, event.displayLabel, false, isShiftDown = false)
            view.onKeyDown(e)
            invalidate()
        } catch (ex: Exception) {
            ex.report()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        val e = MPKeyboardEvent(event.keyCode, event.displayLabel, false, isShiftDown = false)
        view.onKeyUp(e)
        return super.onKeyUp(keyCode, event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            val mouse = Point(event.x.toInt(), event.y.toInt())
            val from = previousMouse ?: mouse

            val e = MPMouseEvent(from, mouse, MPMouseButton.LEFT, 0)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> view.onMouseDown(e)
                MotionEvent.ACTION_UP -> view.onMouseUp(e)
                MotionEvent.ACTION_MOVE -> view.onMouseMove(e)
            }
        } catch (ex: Exception) {
            ex.report()
        }
        return super.onTouchEvent(event)
    }

}
