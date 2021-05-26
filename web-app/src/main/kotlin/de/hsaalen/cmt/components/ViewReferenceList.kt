package de.hsaalen.cmt.components

import de.crusader.extensions.toDate
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.table
import react.dom.td
import react.dom.th
import react.dom.tr
import styled.css
import styled.styledDiv

/**
 * Intended to render a list of files that are found by tags from search component.
 */
class ViewReferenceList : RComponent<ViewReferenceList.Props, RState>() {

    interface Props : RProps {
        var dto: ServerReferenceListDto?
    }

    /**
     * Called when page is rendered.
     */
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
                val refs = props.dto
                if (refs == null) {
                    tr {
                        td {
                            +"Loading..."
                        }
                    }
                } else {
                    for (ref in refs.references) {
                        tr {
                            td {
                                +ref.displayName
                            }
                            td {
                                +ref.labels.joinToString()
                            }
                            td {
                                +ref.dateLastAccess.toDate().toDateString()
                            }
                        }
                    }
                }
            }
        }
    }

}

