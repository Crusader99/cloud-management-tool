package de.hsaalen.cmt.sql.schema

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.datetime
import java.util.*

/**
 * The postgresql table of the revision data.
 */
object RevisionTable : UUIDTable("revision") {
    val item = reference("item", ReferenceTable, onDelete = ReferenceOption.CASCADE) // primary
    var index = integer("index") // primary

    val dateCreation = datetime("date_creation")
    val dateLastAccess = datetime("date_last_access")
    val creator = reference("creator", UserTable)
    val accessCount = long("access_count")
}

/**
 * A data access object for a revision instance.
 */
class RevisionDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RevisionDao>(RevisionTable)

    var item by ReferenceDao referencedOn RevisionTable.item
    var index by RevisionTable.index

    var dateCreation by RevisionTable.dateCreation
    var dateLastAccess by RevisionTable.dateLastAccess
    var creator by UserDao referencedOn RevisionTable.creator
    var accessCount by RevisionTable.accessCount

}
