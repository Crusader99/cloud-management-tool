package de.hsaalen.cmt.pages

import de.hsaalen.cmt.components.ViewResultList
import de.hsaalen.cmt.network.client.Session
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState

/**
 * The main app component.
 */
class OverviewPage : RComponent<OverviewPage.Props, RState>() {

    interface Props : RProps {
        var session: Session
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        ViewResultList.render(this)
    }

}
