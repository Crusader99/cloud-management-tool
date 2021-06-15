import de.hsaalen.cmt.network.dto.objects.LineChangeMode
import de.hsaalen.cmt.views.components.documenteditor.DiffCalculator
import kotlin.test.Test

class TestDiffCalculator {

    private val changedLines = mutableListOf<Pair<Int, LineChangeMode>>()

    @Test
    fun test() {
        val calc = DiffCalculator(::onChangeLine)
        calc.findChangedLines("line-1\nline-2\nline-3")
        println(changedLines)
    }

    fun onChangeLine(lineNumber: Int, lineContent: String, changeMode: LineChangeMode) {
        changedLines += Pair(lineNumber, changeMode)
    }

}
