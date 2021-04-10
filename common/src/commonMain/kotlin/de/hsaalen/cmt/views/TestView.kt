package de.hsaalen.cmt.views

import de.crusader.objects.Point
import de.crusader.objects.Rectangle
import de.crusader.objects.color.Color
import de.crusader.painter.Painter
import de.hsaalen.cmt.views.api.MPView
import de.hsaalen.cmt.views.events.MPMouseEvent

class TestView : MPView() {

    override fun onMouseDown(e: MPMouseEvent) {
    }

    override fun onRepaint(p: Painter) {
        val rec = Rectangle(Point(), p.size)

        p.createRectangle()
            .color(Color.GREEN)
            .filled(true)
            .size(p.size)
            .draw()
        p.createRectangle()
            .color(Color.RED)
            .filled(true)
            .rectangle(rec.reduce(12))
            .draw()
    }

}
