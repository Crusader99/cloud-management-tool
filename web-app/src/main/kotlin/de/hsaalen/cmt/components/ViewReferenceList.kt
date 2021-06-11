package de.hsaalen.cmt.components

import de.crusader.extensions.toDate
import de.crusader.objects.color.Color
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.toCssColor
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.attrs
import react.dom.tr
import styled.*

/**
 * Intended to render a list of files that are found by tags from search component.
 */
class ViewReferenceList : RComponent<ViewReferenceList.Props, RState>() {

    interface Props : RProps {
        var dto: ServerReferenceListDto?
        var onItemOpen: (Reference) -> Unit
    }

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        styledTable {
            css {
                width = 100.pct // Same as 100 %
                borderCollapse = BorderCollapse.collapse // Print separators
            }
            renderTableHead()
            renderTableBody()
        }
    }

    /**
     * Called when the header of the table should be rendered.
     */
    private fun RBuilder.renderTableHead() = styledThead {
        css {
            color = Color.GRAY.toCssColor()
            backgroundColor = (Color.DARK_GRAY * 0.7f).toCssColor()
            fontSize = 15.px
        }

        val columns = arrayOf("Display Name", "Labels", "Last Access")
        tr {
            for (column in columns) {
                styledTh {
                    css {
                        borderRight = "1px solid " + Color.GRAY.hex
                        lastChild {
                            borderRight = "none"
                        }
                    }
                    +column
                }
            }
        }
    }

    /**
     * Called when the complete body of the table should be rendered.
     */
    private fun RBuilder.renderTableBody() = styledTbody {
        css {
            textAlign = TextAlign.start
            backgroundColor = Color.WHITE.toCssColor()
            color = Color.BLACK.toCssColor()
        }
        val dto = props.dto
        if (dto == null) {
            renderTableBodyRow(null) // Currently no references loaded
        } else {
            for (ref in dto.references) {
                renderTableBodyRow(ref)
            }
        }
    }

    /**
     * Called when the only a single row of the table body should be rendered.
     */
    private fun RBuilder.renderTableBodyRow(ref: Reference?) = styledTr {
        val columns = if (ref == null) arrayOf("Loading...") else arrayOf(
            ref.displayName,
            ref.labels.joinToString(),
            ref.dateLastAccess.toDate().toDateString(),
        )

        css {
            fontSize = 15.px
            cursor = Cursor.pointer
            borderBottom = "1px solid " + Color.GRAY.toCssColor()
            hover {
                backgroundColor = Color.GRAY.toCssColor()
            }
        }
        attrs {
            if (ref != null) {
                onClickFunction = { props.onItemOpen(ref) }
            }
        }
        for (column in columns) {
            styledTd {
                css {
                    padding(14.px, 14.px)
                }
                +column
            }
        }
    }
}
