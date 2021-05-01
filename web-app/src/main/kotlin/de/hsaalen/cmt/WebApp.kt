package de.hsaalen.cmt

import de.hsaalen.cmt.components.ViewHeader
import de.hsaalen.cmt.components.ViewResultList
import materialui.styles.themeprovider.themeProvider
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
class WebApp : RComponent<RProps, RState>() {

    override fun RBuilder.render() {
        themeProvider(Themes.LIGHT) {
            header {
                ViewHeader.render(this)
            }
            div {
                h2 { br { } }
                ViewResultList.render(this)
            }
        }
    }

}
