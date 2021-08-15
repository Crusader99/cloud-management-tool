import de.hsaalen.cmt.utils.addCursors
import de.hsaalen.cmt.utils.cursorCharacter
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test calculation of virtual cursors in texts.
 */
class CursorUpdateTest {

    /**
     * Test with randomized cursor positions
     */
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testRandomized() {
        val random = Random(0)
        repeat(1000) {
            val text = "abcdefghjklmnopqrstuvwxyzabcdefghjklmnopqrstuvwxyzabcdefghjklmnopqrstuvwxyz"
            val cursors = buildList<Int>(Random.nextInt(text.length / 2)) {
                random.nextInt()
            }.toSet()

            assertEquals(text, text.addCursors(cursors).replace(cursorCharacter, ""))
        }
    }

    /**
     * Test a specific case that previously failed.
     */
    @Test
    fun testWithSpecificText() {
        val text = "abc"
        val cursors = setOf(1)
        assertEquals(text, text.addCursors(cursors).replace(cursorCharacter, ""))
    }

}
