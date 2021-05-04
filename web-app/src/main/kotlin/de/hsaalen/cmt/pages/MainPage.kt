package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.ViewResultList
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

/**
 * The main app component.
 */
class MainPage : RComponent<RProps, RState>() {

    override fun RBuilder.render() {
        ViewResultList.render(this)
    }

}
