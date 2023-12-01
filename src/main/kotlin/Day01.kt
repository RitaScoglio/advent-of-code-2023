import java.io.File


class Day01 {

    val NUMBERS = ('1'..'9').toList()

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
        println(firstStar(input))
        println(secondStar(input))
    }

    fun firstStar(input: List<String>): Int {
        return input.map { string -> string.filter { it in NUMBERS } }
            .map { it.first().toString() + it.last() }
            .map { it.toInt() }
            .sum()
    }

    fun secondStar(input: List<String>): Int {
        val numbers = input.map { s ->
            s.replace("one", "one1one")
                .replace("two", "two2two")
                .replace("three", "three3three")
                .replace("four", "four4four")
                .replace("five", "five5five")
                .replace("six", "six6six")
                .replace("seven", "seven7seven")
                .replace("eight", "eight8eight")
                .replace("nine", "nine9nine")
        }
        return firstStar(numbers)
    }
}