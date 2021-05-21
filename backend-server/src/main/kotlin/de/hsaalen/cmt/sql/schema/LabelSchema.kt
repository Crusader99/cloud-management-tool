package de.hsaalen.cmt.sql.schema

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

/**
 * The postgresql table of the label data.
 */
object LabelTable : Table("label") {
    val item = reference("item", ReferenceTable, onDelete = ReferenceOption.CASCADE)
    val tagName = varchar("tag_name", 32)

    override val primaryKey = PrimaryKey(item, tagName, name = "id")
}
