package de.hsaalen.cmt.views.components.documenteditor

import de.crusader.extensions.EnumDirection
import de.crusader.objects.Point
import de.crusader.objects.Rectangle
import de.crusader.objects.color.Color
import de.crusader.painter.Painter
import de.crusader.painter.animation.Animator
import de.crusader.painter.animation.EnumInterpolator
import de.hsaalen.cmt.network.session.Session
import de.hsaalen.cmt.views.api.MPView
import de.hsaalen.cmt.views.events.MPKeyboardEvent
import de.hsaalen.cmt.views.events.MPMouseEvent

/**
 * Document editor, implemented in multi-platform code to support multiple targets.
 */
class DocumentEditor(
    defaultText: String = "",
    private val onTextChanged: (String) -> Unit
) : MPView() {

    private val engine = EditorEngine(true, defaultText)

    init {
        Session.instance?.registerListener { dto ->
            // TODO: implement
//            println("Received " + dto.newTextEncrypted)
//            engine.text = dto.newTextEncrypted
        }
    }

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
    private val animator = Animator.withFloat(0f, 5000L, EnumInterpolator.ACCELERATE_DECELERATE)
        .apply {
            value = 1f
        }

    override fun onRepaint(p: Painter) {
        val rec = Rectangle(Point(), p.size)
        p.createRectangle()
            .color(Color.BLACK)
            .filled(true)
            .rectangle(rec.reduce(12))
            .draw()

        val x = 5
        var y = 0
        for (line in engine.lines) {
            val drawString = p.createString()
                .color(Color.WHITE)
                .text(line.toString())
                .rectangle(p.rectangle.location(x, y).timesSize(animator.animation))
                .size(30f)

            drawString.draw()
            y += drawString.textHeight
        }


    }

}
