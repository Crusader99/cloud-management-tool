package de.hsaalen.cmt.sql.schema

import de.hsaalen.cmt.network.dto.objects.ContentType
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.utils.toUUID
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
    val contentType = enumeration("content_type", ContentType::class)
    val owner = reference("owner", UserTable, onDelete = ReferenceOption.CASCADE)
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
    var owner by UserDao referencedOn ReferenceTable.owner
    var labels by LabelDao via LabelRefMappingTable

    /**
     * Convert [ReferenceDao] to [Reference] instance to be transmitted oer network.
     */
    fun toReference(): Reference {
        val now = System.currentTimeMillis()
        val labels = labels.map { it.labelName }.toMutableSet()
        return Reference(id.toUUID(), accessCode, displayName, contentType, now, now, labels)
    }
}
