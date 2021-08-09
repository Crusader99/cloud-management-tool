package de.hsaalen.cmt.components

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.table.*
import de.crusader.extensions.toDate
import de.crusader.objects.color.Color
import de.hsaalen.cmt.events.*
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.network.dto.server.ServerReferenceListDto
import de.hsaalen.cmt.theme.toCssColor
import kotlinx.css.*
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css
import styled.styledDiv

/**
 * Wrapper function to simplify creation of this react component.
 */
fun RBuilder.referenceList(
    dto: ServerReferenceListDto,
) = child(ViewReferenceList::class) {
    attrs {
        this.dto = dto
    }
}

/**
 * React properties of the [ViewReferenceList] component.
 */
external interface ViewReferenceListProps : RProps {
    var dto: ServerReferenceListDto
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
        for (ref in props.dto.references) {
            renderTableBodyRow(ref)
        }
    }

    /**
     * Called when the only a single row of the table body should be rendered.
     */
    private fun RBuilder.renderTableBodyRow(ref: Reference) = mTableRow(hover = true) {
        css {
            cursor = Cursor.pointer
        }
        attrs {
            onClick = { dispatch(it, EventType.PRE_USER_OPEN_REFERENCE, ReferenceEvent(ref)) }
        }

        mTableCell { +ref.displayName }
        mTableCell {
            styledDiv {
                css {
                    display = Display.flex
                }
                for (label in ref.labels) {
                    mChip(label, onDelete = {
                        dispatch(it, EventType.PRE_USER_REMOVE_LABEL, LabelEditEvent(ref, label))
                    }) {
                        attrs {
                            asDynamic().clickable = true
                        }
                    }
                }
                mTooltip("Add label") {
                    mAvatar {
                        attrs {
                            onClick = {
                                dispatch(it, EventType.PRE_USER_ADD_LABEL, ReferenceEvent(ref))
                            }
                        }
                        css {
                            width = 3.spacingUnits
                            height = 3.spacingUnits
                            marginTop = LinearDimension.auto
                            marginBottom = LinearDimension.auto
                        }
                        mIcon("add")
                    }
                }
            }
        }
        mTableCell { +ref.dateLastAccess.toDate().toDateString() }
        mTableCell(align = MTableCellAlign.right) {
            mTooltip("Download") {
                mIconButton("download", onClick = {
                    dispatch(it, EventType.PRE_USER_DOWNLOAD_REFERENCE, ReferenceEvent(ref))
                })
            }
            mTooltip("Rename") {
                mIconButton("edit", onClick = {
                    dispatch(it, EventType.PRE_USER_RENAME_REFERENCE, ReferenceEvent(ref))
                })
            }
            mTooltip("Delete") {
                mIconButton("delete", onClick = {
                    dispatch(it, EventType.PRE_USER_DELETE_REFERENCE, ReferenceEvent(ref))
                })
            }
        }
    }

    /**
     * Execute a custom event in asynchronous way.
     * And prevent parent to receive onClick event, which would open the reference.
     */
    private fun dispatch(origin: org.w3c.dom.events.Event, type: EventType, event: Event) {
        origin.stopPropagation() // Prevent parent to receive onClick event, which would open the reference
        launchNotification(type, event)
    }
}
