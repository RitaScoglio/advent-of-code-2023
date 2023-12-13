import java.io.File
import kotlin.math.pow

class Day12 {

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
            .map { it.split(" ") }
        println(firstStar(input))
        println(secondStar(input))
    }

    fun firstStar(input: List<List<String>>) = input.sumOf { (record, groups) ->
        getArrangements(record, groups.split(',').map { it.toInt() })
    }

    fun secondStar(input: List<List<String>>) = input.sumOf { (record, groups) ->
        getArrangements((1..5).joinToString("?") { record },
            (1..5).map { groups.split(',').map { it.toInt() } }.flatten())
    }

    fun getArrangements(record: String, groups: List<Int>): Long {
        val continuousAreas = record.indices.map { index -> record.drop(index).takeWhile { it != '.' }.length}
        val visited = mutableMapOf<Pair<Int, Int>, Long>()

        fun recursion(recordIndex: Int, groupIndex: Int): Long {
            if (groupIndex == groups.size && record.drop(recordIndex).none { it == '#' })
                return 1L
            else if (recordIndex < record.length && groupIndex < groups.size) {
                if (Pair(recordIndex, groupIndex) !in visited) {
                    var value = 0L
                    if (continuousAreas[recordIndex] >= groups[groupIndex]
                        && (recordIndex + groups[groupIndex] == continuousAreas.size || record[recordIndex + groups[groupIndex]] != '#'))
                        value += recursion(recordIndex + groups[groupIndex] + 1, groupIndex + 1)
                    if (record[recordIndex] != '#')
                        value += recursion(recordIndex + 1, groupIndex)
                    visited[Pair(recordIndex, groupIndex)] = value
                }
                return visited[Pair(recordIndex, groupIndex)]!!
            }
            return 0
        }
        return recursion(0,0)
    }
}
