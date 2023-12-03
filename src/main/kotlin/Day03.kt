import java.io.File

class Day03 {

    data class Coors(val x: Int, val y: Int)

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
        val listOfParts = mutableListOf<Int>()
        val gears = mutableMapOf<Coors, List<Int>>()
        (0..input.size - 1).forEach { i ->
            var j = 0
            while (j < input[1].length) {
                if (input[i][j].isDigit()) {
                    val number = extractNumber(input[i], j)
                    val surroundings = getSurroundings(input, i, j, number.length)

                    //first star
                    if (surroundings.filter { it.second != '.' && !it.second.isDigit() }.size != 0)
                        listOfParts.add(number.toInt())
                    //second start
                    surroundings.filter { it.second == '*' }
                        .forEach { (coors, _) -> gears.merge(coors, listOf(number.toInt())) { new, old -> new + old } }

                    j += number.length
                } else
                    j += 1
            }
        }
        //first star
        println(listOfParts.sum())
        //second star
        println(gears.values.map { if (it.size == 2) it[0] * it[1] else 0 }.sum())
    }

    fun extractNumber(line: String, j: Int): String {
        var number = line[j].toString()
        var current = j + 1
        while (current < line.length) {
            if (line[current].isDigit())
                number += line[current]
            else
                break
            current += 1
        }
        return number
    }

    fun getSurroundings(input: List<String>, row: Int, column: Int, length: Int): MutableList<Pair<Coors, Char>> {
        val surroundings = mutableListOf<Pair<Coors, Char>>()
        (-1..1).forEach { i ->
            (-1..length).forEach { j ->
                surroundings.add(Pair(Coors(row + i, column + j), charAtCoors(input, row + i, column + j)))
            }
        }
        return surroundings
    }

    private fun charAtCoors(input: List<String>, i: Int, j: Int): Char {
        if (i > -1 && i < input.size)
            if (j > -1 && j < input[i].length)
                return input[i][j]
        return '.'
    }
}
