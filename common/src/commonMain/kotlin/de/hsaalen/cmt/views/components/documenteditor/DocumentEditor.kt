package de.hsaalen.cmt.views.components.documenteditor

import de.crusader.extensions.EnumDirection
import de.crusader.objects.Point
import de.crusader.objects.Rectangle
import de.crusader.objects.color.Color
import de.crusader.painter.Painter
import de.crusader.painter.animation.Animator
import de.crusader.painter.animation.EnumInterpolator
import de.crusader.painter.util.EnumRelationType
import de.hsaalen.cmt.network.client.Session
import de.hsaalen.cmt.network.dto.websocket.LiveTextEditDto
import de.hsaalen.cmt.views.api.MPView
import de.hsaalen.cmt.views.events.MPKeyboardEvent
import de.hsaalen.cmt.views.events.MPMouseEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Document editor, implemented in multi-platform code to support multiple targets.
 */
class DocumentEditor(
    defaultText: String = ""
) : MPView() {

    private val engine = EditorEngine(true, EditorEngine.Line(defaultText))

    init {
        Session.instance?.registerListener { dto ->
            println("Received " + dto.newTextEncrypted)
            engine.text = dto.newTextEncrypted
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
            else -> engine.cursor.insert(char.toString())
        }
        GlobalScope.launch {
            val dto = LiveTextEditDto("", engine.text)
            Session.instance?.liveTextEdit(dto)
        }
    }

    // Helper for creating animations
    private val animator = Animator.withFloat(0f, 5000L, EnumInterpolator.ACCELERATE_DECELERATE)
        .apply {
            value = 1f
        }

    override fun onRepaint(p: Painter) {
        val rec = Rectangle(Point(), p.size)

        p.createRectangle()
            .color(Color.GREEN)
            .filled(true)
            .size(p.size)
            .draw()
        p.createRectangle()
            .color(Color.BLACK)
            .filled(true)
            .rectangle(rec.reduce(12))
            .draw()

        p.createString()
            .color(Color.WHITE)
            .text(engine.cursor.line.toString())
            .rectangle(p.rectangle.location(0, 0).timesSize(animator.animation))
            .size(34f)
            .relation(EnumRelationType.CENTER, EnumRelationType.CENTER)
            .filled(true)
            .draw()
    }

}
