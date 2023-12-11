import java.io.File
import kotlin.math.abs

class Day11 {
    data class Coors(val row: Long, val col: Long)

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val rowsToExpand = mutableListOf<Int>()
        val columnsToExpand = mutableListOf<Int>()
        val input = readFileAsList("input.txt")
        (0 until input.size).forEach { index ->
            if ((0 until input.size).map { input[it][index] }.distinct() == listOf('.'))
                columnsToExpand.add(index)
            if ((0 until input.size).map { input[index][it] }.distinct() == listOf('.'))
                rowsToExpand.add(index)
        }
        //firstStar
        println(getDistance(getPairs(getGalaxies(input, rowsToExpand, columnsToExpand, 1))))
        //secondStar
        println(getDistance(getPairs(getGalaxies(input, rowsToExpand, columnsToExpand, 999999))))
    }

    fun getGalaxies(input: List<String>, rowsToExpand: MutableList<Int>, columnsToExpand: MutableList<Int>, multiply: Int): List<Coors> {
        val list = mutableListOf<Coors>()
        input.indices.forEach { row ->
            var col = 0
            while (col < input[row].length) {
                if (input[row][col] == '#') {
                    val expandCol = columnsToExpand.filter { it < col }.size.toLong() * multiply
                    val expandRow = rowsToExpand.filter { it < row }.size.toLong() * multiply
                    list.add(Coors(row + expandRow, col + expandCol))
                }
                col += 1
            }
        }
        return list
    }

    fun getPairs(galaxies: List<Coors>): MutableList<Pair<Coors, Coors>> {
        val pairs = mutableListOf<Pair<Coors, Coors>>()
        galaxies.indices.forEach { i ->
            (i + 1 until galaxies.size).forEach { j ->
                pairs.add(Pair(galaxies[i], galaxies[j]))
            }
        }
        return pairs
    }

    private fun getDistance(pairs: MutableList<Pair<Coors, Coors>>) =
        pairs.sumOf { (one, two) -> abs(one.row - two.row) + abs(one.col - two.col) }
}