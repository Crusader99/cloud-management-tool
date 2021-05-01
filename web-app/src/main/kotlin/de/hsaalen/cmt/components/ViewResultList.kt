package de.hsaalen.cmt.components

import demo.components.header.HeaderProps
import materialui.styles.withStyles
import react.RBuilder
import react.RComponent
import react.RState
import react.dom.div

interface ViewResultListState : RState {
}

/**
 * Intended to render a list of files that are found by tags from search component.
 */
class ViewResultList : RComponent<HeaderProps, ViewResultListState>() {

    override fun ViewResultListState.init() {

    }

    override fun RBuilder.render() {
        div {

        }
    }

    companion object {
        fun render(rBuilder: RBuilder) = with(rBuilder) { styledComponent {} }

        private val styledComponent = withStyles(ViewResultList::class, {
        })
    }

}