package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.documentEditor
import de.hsaalen.cmt.network.Client
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

/**
 * The main app component.
 */
class MainPage : RComponent<MainPage.Props, RState>() {

    interface Props : RProps {
        var client: Client
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        documentEditor("This is a test")
//        ViewResultList.render(this)
    }

}
