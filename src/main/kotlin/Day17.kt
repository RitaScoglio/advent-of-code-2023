import java.io.File
import java.util.PriorityQueue
import kotlin.reflect.KFunction1

class Day17 {

    data class Coors(val row: Int, val col: Int) {
        operator fun plus(other: Coors): Coors {
            return Coors(this.row + other.row, this.col + other.col)
        }
    }

    data class Position(val coors: Coors, val direction: String, val consecutive: Int) {
        fun nextCrucibles(): List<Position> {
            return buildList {
                if (consecutive < 3)
                    addAll(getSameDirection())
                addAll(getOtherDirection())
            }
        }

        fun nextUltraCrucibles(): List<Position> {
            return buildList {
                if (consecutive < 10)
                    addAll(getSameDirection())
                if (consecutive >= 4)
                    addAll(getOtherDirection())
            }
        }

        fun getSameDirection(): List<Position> {
            return buildList {
                when (direction) {
                    "up" -> add(Position(coors + Coors(-1, 0), direction, consecutive + 1))
                    "down" -> add(Position(coors + Coors(1, 0), direction, consecutive + 1))
                    "left" -> add(Position(coors + Coors(0, -1), direction, consecutive + 1))
                    else -> add(Position(coors + Coors(0, 1), direction, consecutive + 1))
                }
            }
        }

        private fun getOtherDirection(): List<Position> {
            return buildList {
                when (direction) {
                    "up", "down" -> {
                        add(Position(coors + Coors(0, 1), "right", 1))
                        add(Position(coors + Coors(0, -1), "left", 1))
                    }
                    else -> {
                        add(Position(coors + Coors(1, 0), "down", 1))
                        add(Position(coors + Coors(-1, 0), "up", 1))
                    }
                }
            }
        }
    }

    data class PositionWithLoss(val heatLoss: Int, val position: Position) : Comparable<PositionWithLoss> {
        override fun compareTo(other: PositionWithLoss): Int {
            return this.heatLoss.compareTo(other.heatLoss)
        }
    }

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt").map { line -> line.toList().map { it.digitToInt() } }
        println(firstStar(input))
        println(secondStar(input))
    }

    fun firstStar(input: List<List<Int>>) =
        getMinimumLoss(input, Position::nextCrucibles,
            listOf(PositionWithLoss(0, Position(Coors(0, 0), "right", 0))), 0)

    fun secondStar(input: List<List<Int>>) =
        getMinimumLoss(input, Position::nextUltraCrucibles,
            listOf(PositionWithLoss(0, Position(Coors(0, 0), "right", 0)),
                PositionWithLoss(0, Position(Coors(0, 0), "down", 0))), 4)

    private fun getMinimumLoss(
        input: List<List<Int>>,
        function: KFunction1<Position, List<Position>>,
        start: List<PositionWithLoss>,
        minConsecutive: Int
    ): Int {
        val queue = PriorityQueue(start)
        val visited = mutableMapOf<Position, Pair<Int, Position>>()
        var end: Int = Int.MAX_VALUE
        while (queue.isNotEmpty() && end == Int.MAX_VALUE) {
            val (heatLoss, current) = queue.remove()
            if (current.coors.row == input.size - 1 && current.coors.col == input[0].size - 1 && current.consecutive >= minConsecutive)
                end = heatLoss
            val next = function(current).distinct().exist(input).filter { it !in visited }
                .map { PositionWithLoss(heatLoss + input[it.coors.row][it.coors.col], it) }
            queue.addAll(next)
            visited.putAll(next.associate { it.position to Pair(it.heatLoss, current) })
        }
        return end
    }

    fun List<Position>.exist(input: List<List<Int>>): List<Position> {
        return this.filter { it.coors.row > -1 && it.coors.col > -1 && it.coors.row < input.size && it.coors.col < input[0].size }
    }
}
