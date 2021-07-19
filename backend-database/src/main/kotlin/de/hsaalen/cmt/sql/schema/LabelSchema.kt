package de.hsaalen.cmt.sql.schema

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

/**
 * The postgresql table of the label data.
 */
object LabelTable : UUIDTable("label") {
    val owner = reference("owner", UserTable, onDelete = ReferenceOption.CASCADE)
    val labelName = varchar("label_name", 64)
}

/**
 * A data access object for a label instance.
 */
class LabelDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<LabelDao>(LabelTable)

    var owner by UserDao referencedOn LabelTable.owner
    var labelName by LabelTable.labelName
}

/**
 * The postgresql table of the label-reference mapping table. Note this is a many-to-many relation.
 */
object LabelRefMappingTable : UUIDTable("label_reference_mapping") {
    val label = reference("label", LabelTable, onDelete = ReferenceOption.CASCADE)
    val reference = reference("reference", ReferenceTable, onDelete = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(label, reference)
}

/**
 * A data access object for a label-reference mapping instance. Note this is a many-to-many relation.
 */
class LabelRefMappingDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<LabelRefMappingDao>(LabelRefMappingTable)

    var label by LabelDao referencedOn LabelRefMappingTable.label
    var reference by ReferenceDao referencedOn LabelRefMappingTable.reference
}

