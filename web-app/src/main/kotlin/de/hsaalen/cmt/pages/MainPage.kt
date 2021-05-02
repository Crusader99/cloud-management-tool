package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.ViewHeader
import de.hsaalen.cmt.components.ViewResultList
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.br
import react.dom.div
import react.dom.h2
import react.dom.header

/**
 * The main app component.
 */
class MainPage : RComponent<RProps, RState>() {

    override fun RBuilder.render() {
        header {
            ViewHeader.render(this)
        }
        div {
            h2 { br { } }
            ViewResultList.render(this)
        }
    }

}
