import java.io.File

class Day14 {
    data class Coors(val row: Int, val col: Int)

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt").map { it.toMutableList() }
        val squared = mutableListOf<Coors>()
        val rounded = mutableListOf<Coors>()
        input.indices.forEach { row ->
            input[row].indices.forEach { col ->
                if (input[row][col] == 'O') {
                    rounded.add(Coors(row, col))
                } else if (input[row][col] == '#')
                    squared.add(Coors(row, col))
            }
        }
        println(firstStar(rounded, squared, input.size))
        println(secondStar(rounded, squared, input.size))
    }

    fun firstStar(rounded: MutableList<Coors>, squared: MutableList<Coors>, size: Int): Int {
        rounded.forEach { coors ->
            rounded.indexOf(coors).let {
                rounded[it] = Coors(moveRockNorth(rounded + squared, rounded[it].row, rounded[it].col),
                    rounded[it].col)
            }
        }
        return rounded.sumOf { size - it.row }
    }

    fun secondStar(rounded: MutableList<Coors>, squared: MutableList<Coors>, size: Int): Int {
        val rooksAfterCycle = mutableMapOf<Set<Coors>, Int>()
        var list = rounded.toSet()
        var i = 0
        while(list !in rooksAfterCycle.keys) {
            rooksAfterCycle[list] = i
            i++
            list = cycle(rounded, squared, size).toSet()
        }
        val start = rooksAfterCycle[list]!!
        return rooksAfterCycle.filter { it.value == start + (1000000000 - start) % (i - start) }.keys.first()
            .sumOf { size - it.row }
    }

    fun moveRockNorth(rocks: List<Coors>, row: Int, col: Int) =
        rocks.filter { it.col == col && it.row < row }.map { it.row }.let { if (it.isEmpty()) -1 else it.max() } + 1

    fun moveRockWest(rocks: List<Coors>, row: Int, col: Int) =
        rocks.filter { it.col < col && it.row == row }.map { it.col }.let { if (it.isEmpty()) -1 else it.max() } + 1

    fun moveRockSouth(rocks: List<Coors>, max: Int, row: Int, col: Int) =
        rocks.filter { it.col == col && it.row > row }.map { it.row }.let { if (it.isEmpty()) max else it.min() } - 1

    fun moveRockEast(rocks: List<Coors>, max: Int, row: Int, col: Int) =
        rocks.filter { it.col > col && it.row == row }.map { it.col }.let { if (it.isEmpty()) max else it.min() } - 1

    fun cycle(rounded: MutableList<Coors>, squared: MutableList<Coors>, size: Int): MutableList<Coors> {
        rounded.sortedBy { it.row }.forEach { coors ->
            rounded.indexOf(coors).let {
                rounded[it] = Coors(moveRockNorth(rounded + squared, rounded[it].row, rounded[it].col),
                    rounded[it].col)
            }
        }
        rounded.sortedBy { it.col }.forEach { coors ->
            rounded.indexOf(coors).let {
                rounded[it] = Coors(rounded[it].row,
                    moveRockWest(rounded + squared, rounded[it].row, rounded[it].col))
            }
        }
        rounded.sortedByDescending { it.row }.forEach { coors ->
            rounded.indexOf(coors).let {
                rounded[it] = Coors(moveRockSouth(rounded + squared, size, rounded[it].row, rounded[it].col),
                    rounded[it].col)
            }
        }
        rounded.sortedByDescending { it.col }.forEach { coors ->
            rounded.indexOf(coors).let {
                rounded[it] = Coors(rounded[it].row,
                    moveRockEast(rounded + squared, size, rounded[it].row, rounded[it].col))
            }
        }
        return rounded
    }
}