package de.hsaalen.cmt.components

import com.ccfraser.muirwik.components.button.mIconButton
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
 * Wrapper function to simplify creation of this react component.
 */
fun RBuilder.referenceList(
    dto: ServerReferenceListDto?,
    onItemOpen: (Reference) -> Unit,
    onItemDelete: (Reference) -> Unit
) = child(ViewReferenceList::class) {
    attrs {
        this.dto = dto
        this.onItemOpen = onItemOpen
        this.onItemDelete = onItemDelete
    }
}

/**
 * React properties of the [ViewReferenceList] component.
 */
private external interface ViewReferenceListProps : RProps {
    var dto: ServerReferenceListDto?
    var onItemOpen: (Reference) -> Unit
    var onItemDelete: (Reference) -> Unit
}

/**
 * Intended to render a list of files that are found by tags from search component.
 */
private class ViewReferenceList : RComponent<ViewReferenceListProps, RState>() {

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

        val columns = arrayOf("Display Name", "Labels", "Last Access", "")
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
        if (ref == null) {
            renderTableBodyColumn("Loading...")
            return@styledTr
        }

        renderTableBodyColumn(ref.displayName)
        renderTableBodyColumn(ref.labels.joinToString())
        renderTableBodyColumn(ref.dateLastAccess.toDate().toDateString())
        styledTd {
            mIconButton("delete", onClick = {
                it.stopPropagation() // Prevent parent element to receive onClick event, which would open the reference
                props.onItemDelete(ref)
            })
        }
    }

    /**
     * Render the text of a single table column.
     */
    private fun RBuilder.renderTableBodyColumn(text: String) = styledTd {
        css {
            padding(14.px, 14.px)
        }
        +text
    }
}
