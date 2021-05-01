package de.hsaalen.cmt.views

import de.crusader.objects.Point
import de.crusader.objects.Rectangle
import de.crusader.objects.color.Color
import de.crusader.painter.Painter
import de.crusader.painter.animation.Animator
import de.crusader.painter.animation.EnumInterpolator
import de.crusader.painter.util.EnumRelationType
import de.hsaalen.cmt.views.api.MPView
import de.hsaalen.cmt.views.events.MPMouseEvent

class TestView : MPView() {

    override fun onMouseDown(e: MPMouseEvent) {
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
            .text("Hello World ;)")
            .rectangle(p.rectangle.location(0, 0).timesSize(animator.animation))
            .size(34f)
            .relation(EnumRelationType.CENTER, EnumRelationType.CENTER)
            .filled(true)
            .draw()
    }

}
