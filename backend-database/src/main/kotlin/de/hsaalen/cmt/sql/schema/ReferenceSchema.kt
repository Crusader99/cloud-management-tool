package de.hsaalen.cmt.sql.schema

import de.hsaalen.cmt.network.dto.objects.Reference
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.util.*

/**
 * The postgresql table of the reference data.
 */
object ReferenceTable : UUIDTable("reference") {
    val accessCode = varchar("access_code", 32) //.uniqueIndex()
    val displayName = varchar("display_name", 512)
    val contentType = varchar("content_type", 32)
    // val latestRevision = reference("latest_revision", RevisionTable, onDelete = ReferenceOption.RESTRICT)
    // TODO fix initialization problem
}

/**
 * A data access object for a reference instance.
 */
class ReferenceDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ReferenceDao>(ReferenceTable)

    var accessCode by ReferenceTable.accessCode
    var displayName by ReferenceTable.displayName
    var contentType by ReferenceTable.contentType

    fun toReference() : Reference{
        val now = System.currentTimeMillis()
        val labels = listOf("note") // TODO implement
        return Reference(id.value.toString(), accessCode, displayName, contentType, now, now, labels)
    }
}
