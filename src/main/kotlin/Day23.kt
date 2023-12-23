import java.io.File

class Day23 {
    data class Coors(val row: Int, val col: Int) {
        override fun toString(): String = "(${row}, ${col})"
    }

    data class WeightedPath(val path: List<Coors>, val weight: Int) : Comparable<WeightedPath> {
        override fun compareTo(other: WeightedPath): Int {
            return this.weight.compareTo(other.weight)
        }
    }

    enum class Direction(val char: Char) {
        UP('^') {
            override val move = Coors(-1, 0)
        },
        DOWN('v') {
            override val move = Coors(1, 0)
        },
        LEFT('<') {
            override val move = Coors(0, -1)
        },
        RIGHT('>') {
            override val move = Coors(0, 1)
        };

        operator fun plus(other: Coors): Coors {
            return Coors(this.move.row + other.row, this.move.col + other.col)
        }

        abstract val move: Coors
    }

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val tiles = mutableMapOf<Coors, Char>()
        val input = readFileAsList("input.txt")
        (input.indices).forEach { row ->
            (0 until input[row].length).forEach { col ->
                if (input[row][col] != '#')
                    tiles[Coors(row, col)] = input[row][col]
            }
        }
        solvePuzzle(tiles, Pair(input.size, input[0].length), true)
        solvePuzzle(tiles, Pair(input.size, input[0].length), false)
    }

    fun solvePuzzle(tiles: MutableMap<Coors, Char>, maxSize: Pair<Int, Int>, slope: Boolean): Int {
        val paths = mutableMapOf<Pair<Coors, Coors>, Int>()
        val visited = mutableSetOf<Coors>()
        var queue = mutableListOf(listOf(Coors(0, 1)))
        while (queue.isNotEmpty()) {
            val newQueue = mutableListOf<List<Coors>>()
            val skip = queue.map { it.first() }.filter { it in visited }.distinct()
            queue.forEach { path ->
                if (path.first() !in skip) {
                    visited.add(path.first())
                    val (intersection, nextNodes, size) = nextIntersection(tiles, path, slope)
                    if (nextNodes.size > 0) {
                        paths[Pair(path.first(), intersection)] = size
                        newQueue.addAll(nextNodes.map { listOf(intersection, it) })
                    }
                }
            }
            queue = newQueue
        }
        return findLongestPath(paths, maxSize)
    }

    fun nextIntersection(
        tiles: MutableMap<Coors, Char>,
        node: List<Coors>,
        slope: Boolean
    ): Triple<Coors, MutableList<Coors>, Int> {
        val path = node.toMutableList()
        val newNodes = mutableListOf<Coors>()
        var size = 0
        while (newNodes.isEmpty()) {
            if (tiles[path.last()] != '.' && slope) {
                val next = Direction.values().first { it.char == tiles[path.last()] } + path.last()
                if (next !in path)
                    path += listOf(next)
                else
                    break
            } else {
                val next = Direction.values().map { it + path.last() }.filter { it in tiles && it !in path }
                if (next.isEmpty())
                    newNodes.add(path.last())
                if (next.size == 1)
                    path += listOf(next.first())
                else newNodes.addAll(next)
            }
            size += 1
        }
        return Triple(path.last(), newNodes, size)
    }

    fun findLongestPath(graph: MutableMap<Pair<Coors, Coors>, Int>, input: Pair<Int, Int>): Int {
        val pathSizes = mutableListOf<Int>()
        val queue = mutableListOf(WeightedPath(listOf(Coors(0, 1)), 0))
        while (queue.isNotEmpty()) {
            val (path, size) = queue.first()
            queue.removeFirst()
            if (path.last() == Coors(input.first - 1, input.second - 2))
                pathSizes.add(size - 1)
            graph.filter { edge -> path.last() == edge.key.first && edge.key.second !in path }
                .forEach { (neighEdge, edgeSize) ->
                    queue.add(WeightedPath(path + listOf(neighEdge.second), size + edgeSize))
                }
        }
        return pathSizes.max()
    }
}
