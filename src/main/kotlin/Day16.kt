import java.io.File

class Day16 {

    data class Coors(val row: Int, val col: Int) {
        operator fun plus(other: Direction): Coors {
            return Coors(this.row + other.move.row, this.col + other.move.col)
        }
    }

    data class Position(val coors: Coors, val direction: Direction)

    enum class Direction() {
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
        println(firstStar(input))
        println(secondStar(input))
    }

    fun firstStar(input: List<String>) = energizeTiles(input, Position(Coors(0, 0), Direction.RIGHT))

    fun secondStar(input: List<String>) = (1 until input.size - 1).maxOf { i ->
        maxOf(
            energizeTiles(input, Position(Coors(0, i), Direction.DOWN)),
            energizeTiles(input, Position(Coors(input.size - 1, i), Direction.UP)),
            energizeTiles(input, Position(Coors(i, 0), Direction.RIGHT)),
            energizeTiles(input, Position(Coors(i, input.size - 1), Direction.LEFT))
        )
    }

    fun energizeTiles(input: List<String>, start: Position): Int {
        val energized = mutableListOf<Position>()
        val queue = mutableListOf<Position>()
        queue.add(start)
        while (queue.isNotEmpty()) {
            val position = queue.first()
            queue.removeFirst()
            if (position !in energized && insideLayout(position.coors, input.size, input[0].length)) {
                energized.add(position)
                queue.addAll(
                    when (input[position.coors.row][position.coors.col]) {
                        '/' -> rightMirror(position.coors, position.direction)
                        '\\' -> leftMirror(position.coors, position.direction)
                        '-' -> horizontalSplitter(position.coors, position.direction)
                        '|' -> verticalSplitter(position.coors, position.direction)
                        else -> listOf(Position(position.coors + position.direction, position.direction))
                    }
                )
            }
        }
        return energized.map { it.coors }.toSet().size
    }

    private fun insideLayout(coors: Coors, maxRow: Int, maxCol: Int): Boolean =
        (coors.row > -1 && coors.col > -1 && coors.row < maxRow && coors.col < maxCol)

    fun rightMirror(coors: Coors, direction: Direction): List<Position> {
        val newDirection = when (direction) {
            Direction.UP -> Direction.RIGHT
            Direction.DOWN -> Direction.LEFT
            Direction.LEFT -> Direction.DOWN
            else -> Direction.UP
        }
        return listOf(Position(coors + newDirection, newDirection))
    }

    fun leftMirror(coors: Coors, direction: Direction): List<Position> {
        val newDirection = when (direction) {
            Direction.UP -> Direction.LEFT
            Direction.DOWN -> Direction.RIGHT
            Direction.LEFT -> Direction.UP
            else -> Direction.DOWN
        }
        return listOf(Position(coors + newDirection, newDirection))
    }

    fun horizontalSplitter(coors: Coors, direction: Direction): List<Position> {
        val newDirection = when (direction) {
            Direction.UP, Direction.DOWN -> listOf(Direction.LEFT, Direction.RIGHT)
            else -> listOf(direction)
        }
        return newDirection.map { Position(coors + it, it) }
    }

    fun verticalSplitter(coors: Coors, direction: Direction): List<Position> {
        val newDirection = when (direction) {
            Direction.RIGHT, Direction.LEFT -> listOf(Direction.UP, Direction.DOWN)
            else -> listOf(direction)
        }
        return newDirection.map { Position(coors + it, it) }
    }

}