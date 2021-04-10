package de.hsaalen.cmt.views.events

import de.crusader.objects.Point

class MPMouseEvent(
    val from: Point,
    val mouse: Point,
    val button: MPMouseButton,
    val clicks: Int
)
