package de.hsaalen.cmt.components

import de.crusader.objects.Point
import de.crusader.objects.position
import de.crusader.painter.draw
import de.hsaalen.cmt.extensions.coroutines
import de.hsaalen.cmt.views.api.MPView
import de.hsaalen.cmt.views.events.MPKeyboardEvent
import de.hsaalen.cmt.views.events.MPMouseButton
import de.hsaalen.cmt.views.events.MPMouseEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.css.pct
import kotlinx.css.width
import kotlinx.html.tabIndex
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import react.*
import react.dom.attrs
import styled.css
import styled.styledCanvas

/**
 * Wrapper function to simplify creation of this react component.
 */
fun RBuilder.canvasRenderer(view: MPView) =
    child(ViewCanvasRenderer::class) {
        attrs {
            this.view = view
        }
    }

/**
 * React properties of the [ViewCanvasRenderer] component.
 */
private external interface ViewCanvasRendererProps : RProps {
    var view: MPView
}

/**
 * A React component for rendering canvas elements which supports multi-platform support.
 */
private class ViewCanvasRenderer : RComponent<ViewCanvasRendererProps, RState>() {
    private var previousMouse: Point? = null
    private var refreshJob: Job? = null
    private var canvasRef = createRef<HTMLCanvasElement>()


    /**
     * Called only once when component was created.
     */
    override fun componentDidMount() {
        refreshJob?.cancel()
        canvasRef.current?.onmousemove = { props.view.onMouseMove(it.toMultiPlatform()) }
        canvasRef.current?.onmousedown = { props.view.onMouseDown(it.toMultiPlatform()) }
        canvasRef.current?.onmouseup = { props.view.onMouseUp(it.toMultiPlatform()) }
        canvasRef.current?.onkeydown = { props.view.onKeyDown(it.toMultiPlatform()) }
        canvasRef.current?.onkeyup = { props.view.onKeyUp(it.toMultiPlatform()) }
        refreshJob = coroutines.launch {
            while (isActive) {
                delay(60)
                canvasRef.current?.draw { p ->
                    props.view.onRepaint(p)
                }
            }
        }
    }

    /**
     * When component is no longer used.
     */
    override fun componentWillUnmount() {
        refreshJob?.cancel()
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        styledCanvas {
            attrs {
                ref = canvasRef
                tabIndex = "1"
            }
            css {
                width = 100.pct
            }
        }
    }

    /**
     * Convert JS keyboard event to multiplatform keyboard event instance.
     */
    private fun KeyboardEvent.toMultiPlatform() =
        MPKeyboardEvent(
            this.keyCode,
            this.charCode.toChar(),
            isControlDown = false,
            isShiftDown = false
        )

    /**
     * Convert JS mouse event to multiplatform mouse event instance.
     */
    private fun MouseEvent.toMultiPlatform() =
        MPMouseEvent(this.position, this.position, MPMouseButton.LEFT, 1)

}
