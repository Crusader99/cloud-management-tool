package de.hsaalen.cmt.components

import materialui.styles.withStyles
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import styled.css
import styled.styledDiv

interface ViewResultListState : RState {
}

/**
 * Intended to render a list of files that are found by tags from search component.
 */
class ViewResultList : RComponent<RProps, ViewResultListState>() {

    override fun ViewResultListState.init() {

    }

    override fun RBuilder.render() {
        styledDiv {
            attrs {
                css {

                }
            }
            table {
                tr {
                    th {
                        +"Display Name"
                    }
                    th {
                        +"Labels"
                    }
                    th {
                        +"Last Access"
                    }
                }
                repeat(2000){
                    tr {
                        td {
                            +"A name"
                        }
                        td {
                            +"Note"
                        }
                        td{
                            +"19.10.2020"
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun render(rBuilder: RBuilder) = rBuilder.run { styledComponent {} }

        private val styledComponent = withStyles(ViewResultList::class, {
        })
    }

}
