package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.ViewResultList
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

/**
 * The main app component.
 */
class MainPage : RComponent<MainPage.Props, RState>() {

    interface Props : RProps {
//        val client: Client
    }

    override fun RState.init() {

    }

    override fun RBuilder.render() {
        ViewResultList.render(this)
    }

}
