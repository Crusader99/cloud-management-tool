package de.hsaalen.cmt.repository

import de.hsaalen.cmt.events.GlobalEventDispatcher
import de.hsaalen.cmt.events.LabelChangeEvent
import de.hsaalen.cmt.network.dto.objects.LabelChangeMode
import de.hsaalen.cmt.network.dto.objects.UUID
import de.hsaalen.cmt.network.dto.websocket.LabelUpdateDto
import de.hsaalen.cmt.sql.schema.*
import de.hsaalen.cmt.utils.id
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

/**
 * Executes database operations for the label management.
 */
internal class LabelRepositoryImpl(private val userEmail: String) : LabelRepository {

    /**
     * Add label to an existing reference by it's [UUID].
     */
    override suspend fun addLabel(reference: UUID, labelName: String) {
        newSuspendedTransaction {
            val creator = UserDao.findUserByEmail(userEmail)
            val ref = findReference(reference) ?: throw IllegalStateException("Reference not found: $reference")
            val label = findLabel(creator, labelName) ?: LabelDao.new {
                this.labelName = labelName
                this.owner = creator
            }
            LabelRefMappingDao.new {
                this.label = label
                this.reference = ref
            }
        }

        // Call event handlers
        val dto = LabelUpdateDto(reference, labelName, LabelChangeMode.ADD)
        GlobalEventDispatcher.notify(LabelChangeEvent(dto, userEmail))
    }

    /**
     * Remove label from an existing reference by it's [UUID].
     */
    override suspend fun removeLabel(reference: UUID, labelName: String) {
        newSuspendedTransaction {
            val creator = UserDao.findUserByEmail(userEmail)
            val ref = findReference(reference) ?: throw IllegalStateException("Reference not found: $reference")
            val label = findLabel(creator, labelName) ?: throw IllegalStateException("Label not found: $labelName")

            // Remove label from reference
            val removeQuery = (LabelRefMappingTable.label eq label.id) and (LabelRefMappingTable.reference eq ref.id)
            LabelRefMappingTable.deleteWhere { removeQuery }

            // Cleanup label when used nowhere
            if (LabelRefMappingDao.find(LabelRefMappingTable.label eq label.id).count() == 0L) {
                LabelTable.deleteWhere { LabelTable.id eq label.id }
            }
        }

        // Call event handlers
        val dto = LabelUpdateDto(reference, labelName, LabelChangeMode.DELETE)
        GlobalEventDispatcher.notify(LabelChangeEvent(dto, userEmail))
    }

    /**
     * List all labels from a user that are applied to any reference.
     */
    override suspend fun listLabels(): List<String> {
        return newSuspendedTransaction {
            val user = UserDao.findUserByEmail(userEmail)
            LabelDao.find(LabelTable.owner eq user.id).map { it.labelName }
        }
    }

    /**
     * Find a [LabelDao] instance from database by given owner and label name.
     * Will return null when no found in database.
     */
    private fun findLabel(user: UserDao, labelName: String) =
        LabelDao.find((LabelTable.owner eq user.id) and (LabelTable.labelName eq labelName)).singleOrNull()

    /**
     * Find a [ReferenceDao] instance from database by given [UUID].
     * Will return null when no found in database.
     */
    private fun findReference(uuid: UUID) = ReferenceDao.findById(uuid.id)

}
