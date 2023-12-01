import java.io.File


class Day01 {

    val NUMBERS = ('1'..'9').toList()

    val WORD_NUMBERS = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
        println(firstStar(input))
        println(secondStar(input))
    }

    fun firstStar(input: List<String>): Int {
        return input.map { line -> line.filter { it in NUMBERS }.let { (it.first().toString() + it.last()).toInt() } }
            .sum()
    }

    fun secondStar(input: List<String>): Int = input.map { line ->
        replaceWordWithNumber(line, String::findAnyOf)?.let {
            replaceWordWithNumber(
                it, String::findLastAnyOf
            )
        }
    }.let { firstStar(it as List<String>) }

    fun replaceWordWithNumber(line: String, function: (String, Collection<String>) -> Pair<Int, String>?): String? =
        function(line, WORD_NUMBERS)
            ?.let { (index, number) ->
                line.take(index) + (WORD_NUMBERS.indexOf(number) + 1) + line.drop(index)
            }
}

