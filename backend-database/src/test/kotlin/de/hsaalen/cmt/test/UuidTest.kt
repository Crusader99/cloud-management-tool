package de.hsaalen.cmt.test

import de.hsaalen.cmt.network.dto.objects.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Basic tests for [UUID] functionality.
 */
class UuidTest {

    /**
     * The conversion between string and [UUID]
     */
    @Test
    fun testToString() {
        val id = UUID("05762cca-2dc1-4dee-a4ff-401c293debfb")
        assertEquals(id.value, id.toString())
    }

}
