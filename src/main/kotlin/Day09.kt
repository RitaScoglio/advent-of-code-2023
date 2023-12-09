import java.io.File

class Day09 {
    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
            .map { line -> line.split(" ").map { it.toInt() } }
            .map { getListOfSequences(it) }
        println(firstStar(input))
        println(secondStar(input))
    }

    fun firstStar(input: List<MutableList<List<Int>>>) =
        input.map { it.fold(0) { acc, e -> acc + e.last() } }.sum()

    fun secondStar(input: List<MutableList<List<Int>>>) =
        input.map { it.foldRight(0) { e, acc -> e.first() - acc } }.sum()

    fun getListOfSequences(sequence: List<Int>): MutableList<List<Int>> {
        val listOfSequences = mutableListOf(sequence)
        while (listOfSequences.last().distinct() != listOf(0)) {
            listOfSequences.add(listOfSequences.last().windowed(2, 1).map { pair -> pair[1] - pair[0] })
        }
        return listOfSequences
    }
}