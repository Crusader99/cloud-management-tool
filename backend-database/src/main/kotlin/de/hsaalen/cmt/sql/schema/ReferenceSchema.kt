package de.hsaalen.cmt.sql.schema

import de.hsaalen.cmt.network.dto.objects.ContentType
import de.hsaalen.cmt.network.dto.objects.Reference
import de.hsaalen.cmt.utils.toUUID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.jodatime.datetime
import java.util.*

/**
 * The postgresql table of the reference data.
 */
object ReferenceTable : UUIDTable("reference") {
    val displayName = varchar("display_name", 512)
    val contentType = enumeration("content_type", ContentType::class)
    val owner = reference("owner", UserTable, onDelete = ReferenceOption.CASCADE)
    val dateLastModified = datetime("date_last_modified")
}

/**
 * A data access object for a reference instance.
 */
class ReferenceDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ReferenceDao>(ReferenceTable)

    var displayName by ReferenceTable.displayName
    var contentType by ReferenceTable.contentType
    var owner by UserDao referencedOn ReferenceTable.owner
    var dateLastModified by ReferenceTable.dateLastModified
    var labels by LabelDao via LabelRefMappingTable

    /**
     * Convert [ReferenceDao] to [Reference] instance to be transmitted oer network.
     */
    fun toReference(): Reference {
        val labels = labels.map { it.labelName }.toMutableSet()
        return Reference(id.toUUID(), displayName, contentType, dateLastModified.millis, labels)
    }
}
