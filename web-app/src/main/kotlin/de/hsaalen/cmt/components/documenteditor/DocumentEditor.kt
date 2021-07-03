package de.hsaalen.cmt.components.documenteditor

import de.crusader.extensions.EnumDirection
import de.crusader.objects.Point
import de.crusader.objects.Rectangle
import de.crusader.painter.Painter
import de.crusader.painter.animation.Animator
import de.crusader.painter.animation.EnumInterpolator
import de.hsaalen.cmt.Theme
import de.hsaalen.cmt.views.api.MPView
import de.hsaalen.cmt.views.events.MPKeyboardEvent
import de.hsaalen.cmt.views.events.MPMouseEvent

/**
 * Document editor, implemented in multi-platform code to support multiple targets.
 */
class DocumentEditor(
    private val engine: EditorEngine,
    private val onTextChanged: (String) -> Unit,
) : MPView() {

    override fun onMouseDown(e: MPMouseEvent) {
    }

    override fun onKeyDown(e: MPKeyboardEvent) {
        val char = e.keyCode.toChar()
        when {
            e.isArrowLeft -> engine.cursor.move(EnumDirection.LEFT)
            e.isArrowRight -> engine.cursor.move(EnumDirection.RIGHT)
            e.isArrowUp -> engine.cursor.move(EnumDirection.UP)
            e.isArrowDown -> engine.cursor.move(EnumDirection.DOWN)
            e.isBackspace -> engine.cursor.deletePreviousChar()
            e.isDelete -> engine.cursor.deleteFollowingChar()
            e.isEnter -> engine.cursor.newLine()
            else -> engine.cursor.insert(char.toString())
        }
        onTextChanged(engine.text)
    }

    // Helper for creating animations
    private val animator = Animator.withFloat(0f, 5000L, EnumInterpolator.ACCELERATE_DECELERATE).apply {
        value = 1f
    }

    override fun onRepaint(p: Painter) {
        val rec = Rectangle(Point(), p.size)
        p.createRectangle()
            .color(Theme.current.backgroundColor)
            .filled(true)
            .rectangle(rec.reduce(12))
            .draw()

        val x = 5
        var y = 5
        for (line in engine.lines) {
            val drawString = p.createString()
                .color(Theme.current.textColor)
                .text(line.toString())
                .rectangle(p.rectangle.location(x, y).timesSize(animator.animation))
                .size(30f)

            drawString.draw()
            y += drawString.textHeight
        }


    }

}
