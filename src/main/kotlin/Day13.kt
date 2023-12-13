import java.io.File

class Day13 {

    fun readFileAsString(fileName: String): String = File(fileName).readText(Charsets.UTF_8)

    fun main() {
        val input = readFileAsString("input.txt")
            .split("\n\n")
            .map { it.split('\n') }
        //firstStar
        println(stars(input, 0))
        //secondStar
        println(stars(input, 1))
    }

    fun stars(input: List<List<String>>, smudges: Int) =
        getReflections(input, smudges, ::horizontalSize, ::horizontalLine).sumOf { it * 100 } +
                getReflections(input, smudges, ::verticalSize, ::verticalLine).sumOf { it }

    fun getReflections(
        input: List<List<String>>,
        allowedSmudges: Int,
        size: (List<String>) -> Int,
        line: (List<String>, Int) -> String
    ): MutableList<Int> {
        val listReflections = MutableList(input.size) { 0 }
        input.indices.forEach { index ->
            var i = 0
            while (i < size(input[index]) - 1) {
                val smudges = smudges(line(input[index], i), line(input[index], i + 1))
                if (smudges <= allowedSmudges
                    && checkReflection(input[index], i, allowedSmudges - smudges, size(input[index]), line)) {
                    listReflections[index] = i + 1
                    break
                }
                i++
            }
        }
        return listReflections
    }

    fun smudges(s1: String, s2: String) = s1.indices.sumOf { if (s1[it] == s2[it]) 0.toInt() else 1 }

    fun horizontalSize(lines: List<String>) = lines.size

    fun verticalSize(lines: List<String>) = lines[0].length

    fun horizontalLine(lines: List<String>, i: Int) = lines[i]

    fun verticalLine(lines: List<String>, i: Int) = lines.map { it[i] }.toString()

    fun checkReflection(
        lines: List<String>,
        i: Int,
        s: Int,
        length: Int,
        getLine: (List<String>, Int) -> String
    ): Boolean {
        var oneSide = i - 1
        var otherSide = i + 2
        var smudge = s
        while (oneSide >= 0 && otherSide < length) {
            smudge -= smudges(getLine(lines, oneSide), getLine(lines, otherSide))
            if (smudge < 0)
                return false
            oneSide -= 1
            otherSide += 1
        }
        return smudge <= 0
    }
}
