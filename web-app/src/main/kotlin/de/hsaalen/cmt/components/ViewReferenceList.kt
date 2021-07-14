package de.hsaalen.cmt.components

import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.mChip
import com.ccfraser.muirwik.components.mPaper
import com.ccfraser.muirwik.components.mTooltip
import com.ccfraser.muirwik.components.table.*
import de.crusader.extensions.toDate
import de.crusader.objects.color.Color
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.toCssColor
import kotlinx.css.Cursor
import kotlinx.css.backgroundColor
import kotlinx.css.cursor
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css

/**
 * Wrapper function to simplify creation of this react component.
 */
fun RBuilder.referenceList(
    dto: ServerReferenceListDto?,
    onItemOpen: (Reference) -> Unit,
    onItemDownload: (Reference) -> Unit,
    onItemDelete: (Reference) -> Unit
) = child(ViewReferenceList::class) {
    attrs {
        this.dto = dto
        this.onItemOpen = onItemOpen
        this.onItemDownload = onItemDownload
        this.onItemDelete = onItemDelete
    }
}

/**
 * React properties of the [ViewReferenceList] component.
 */
external interface ViewReferenceListProps : RProps {
    var dto: ServerReferenceListDto?
    var onItemOpen: (Reference) -> Unit
    var onItemDownload: (Reference) -> Unit
    var onItemDelete: (Reference) -> Unit
}

/**
 * Intended to render a list of files that are found by tags from search component.
 */
@JsExport
class ViewReferenceList : RComponent<ViewReferenceListProps, RState>() {

    /**
     * Called when page is rendered.
     */
    override fun RBuilder.render() {
        mPaper {
            mTableContainer {
                mTable {
                    attrs {
                        asDynamic().size = "small"
                    }
                    renderTableHead()
                    renderTableBody()
                }
            }
            mTablePagination(
                rowsPerPageOptions = arrayOf(10, 25, 100),
                component = "div",
                page = 0,
                count = 23,
                rowsPerPage = 10,
            )
        }
    }

    /**
     * Called when the header of the table should be rendered.
     */
    private fun RBuilder.renderTableHead() = mTableHead {
        val columns = arrayOf("Display Name", "Labels", "Last Access", "")
        mTableRow {
            for (column in columns) {
                mTableCell {
                    css {
                        backgroundColor = Color.DARK_GRAY.toCssColor()
                    }
                    +column
                }
            }
        }
    }

    /**
     * Called when the complete body of the table should be rendered.
     */
    private fun RBuilder.renderTableBody() = mTableBody {
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
    private fun RBuilder.renderTableBodyRow(ref: Reference?) = mTableRow(hover = true) {
        css {
            cursor = Cursor.pointer
        }
        attrs {
            if (ref != null) {
                onClick = { props.onItemOpen(ref) }
            }
        }
        if (ref == null) {
            mTableCell { +"Loading..." }
            return@mTableRow
        }

        mTableCell { +ref.displayName }
        mTableCell {
            for (label in ref.labels) {
                mChip(label) {
                    attrs {
                        asDynamic().clickable = true
                    }
                }
            }
        }
        mTableCell { +ref.dateLastAccess.toDate().toDateString() }
        mTableCell(align = MTableCellAlign.right) {
            mTooltip("Download") {
                mIconButton("download", onClick = {
                    it.stopPropagation() // Prevent parent to receive onClick event, which would open the reference
                    props.onItemDownload(ref)
                })
            }
            mTooltip("Delete") {
                mIconButton("delete", onClick = {
                    it.stopPropagation() // Prevent parent to receive onClick event, which would open the reference
                    props.onItemDelete(ref)
                })
            }
        }
    }
}
