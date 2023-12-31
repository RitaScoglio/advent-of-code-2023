import java.io.File
import kotlin.math.min
import kotlin.math.pow

class Day21 {
    data class Coors(val row: Int, val col: Int) {
        operator fun plus(direction: Direction): Coors {
            return Coors(this.row + direction.move.row, this.col + direction.move.col)
        }

        operator fun compareTo(marginMax: Coors): Int {
            return min(this.row.compareTo(marginMax.row), this.col.compareTo(marginMax.col))
        }

        fun modulate(rowMax: Int, colMax: Int): Coors {
            return Coors(this.row.mod(rowMax), this.col.mod(colMax))
        }
    }

    enum class Direction {
        UP {
            override val move = Coors(-1, 0)
        },
        DOWN {
            override val move = Coors(1, 0)
        },
        LEFT {
            override val move = Coors(0, -1)
        },
        RIGHT {
            override val move = Coors(0, 1)
        };

        abstract val move: Coors
    }

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
        var start = Coors(0, 0)
        val rocks = mutableListOf<Coors>()
        (0 until input.size).forEach { row ->
            (0 until input[row].length).forEach { col ->
                if (input[row][col] == 'S')
                    start = Coors(row, col)
                else if (input[row][col] == '#')
                    rocks.add(Coors(row, col))
            }
        }
        println(firstStar(rocks, mutableListOf(Pair(0, start)), mutableMapOf<Coors, Int>(), input.size, 64))
        println(secondStar(start, rocks, input.size).toLong())
    }

    private fun firstStar(
        rocks: MutableList<Coors>,
        queue: MutableList<Pair<Int, Coors>>,
        visited: MutableMap<Coors, Int>,
        margin: Int,
        maxSteps: Int
    ): Int {
        while (queue.isNotEmpty()) {
            val (steps, coors) = queue.first()
            queue.removeFirst()
            if (coors !in visited) {
                visited[coors] = steps
                if (steps < maxSteps) {
                    queue.addAll(moveForward(coors, margin, steps, rocks))
                }
            }
        }
        return visited.filter { (_, steps) -> steps % 2 == maxSteps % 2 }.size
    }

    private fun secondStar(start: Coors, rocks: MutableList<Coors>, size: Int): Double {
        val steps = 26501365 % size
        val sequence = mutableListOf<Pair<Int, Int>>()
        val visited = mutableMapOf<Coors, Int>()
        val queue = mutableListOf(Pair(0, start))
        (0..2).forEach { iteration ->
            val currentMaxSteps = steps + (iteration * size)
            sequence.add(Pair(iteration, firstStar(rocks, queue, visited, size, currentMaxSteps)))
            queue.addAll(visited.filter { (_, steps) -> steps == currentMaxSteps }
                .map { (coors, steps) -> moveForward(coors, size, steps, rocks) }.flatten())
        }
        val (c, b, a) = leastSquareQuadratic(sequence)
        val x = (26501365 - steps) / size.toDouble()
        return a * x.pow(2) + b * x + c
    }

    private fun moveForward(
        coors: Coors,
        max: Int,
        steps: Int,
        rocks: MutableList<Coors>
    ): MutableList<Pair<Int, Coors>> {
        val nextSteps = mutableListOf<Pair<Int, Coors>>()
        Direction.values().forEach { direction ->
            (coors + direction).let {
                if (it.modulate(max, max) !in rocks)
                    nextSteps.add(Pair(steps + 1, it))
            }
        }
        return nextSteps
    }

    private fun leastSquareQuadratic(sequence: MutableList<Pair<Int, Int>>): Triple<Double, Double, Double> {
        val matrix = (0..2).map { row ->
            (0..2).map { col ->
                (0..2).sumOf {
                    sequence[it].first.toDouble().pow(row) * sequence[it].first.toDouble().pow(col)
                }
            }
        }
        val B = (0..2).map { row ->
            (0..2).sumOf {
                sequence[it].first.toDouble().pow(row) * sequence[it].second
            }
        }
        val determinant = computeDeterminant(matrix)
        val first = computeDeterminant((0..2).map { listOf(B[it]) + matrix[it].drop(1) })
        val second = computeDeterminant((0..2).map { matrix[it].take(1) + listOf(B[it]) + matrix[it].takeLast(1) })
        val third = computeDeterminant((0..2).map { matrix[it].dropLast(1) + listOf(B[it]) })
        return Triple(first / determinant, second / determinant, third / determinant)
    }

    fun computeDeterminant(matrix: List<List<Double>>) = sarus(matrix, ::add) - sarus(matrix, ::subtract)

    fun sarus(matrix: List<List<Double>>, function: (Int, Int) -> Int) =
        (0..2).sumOf { row ->
            (0..2).map { col ->
                matrix[(function(row, col)).mod(3)][col]
            }.reduce { acc, i -> acc * i }
        }

    fun add(number1: Int, number2: Int) = number1 + number2

    fun subtract(number1: Int, number2: Int) = number1 - number2
}