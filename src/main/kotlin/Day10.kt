import java.io.File

class Day10 {

    data class Coors(val row: Int, val col: Int) {
        operator fun plus(other: Coors): Coors {
            return Coors(this.row + other.row, this.col + other.col)
        }

        fun exists(coors: Coors): Boolean {
            return row >= 0 && col >= 0 && row < coors.row && col < coors.col
        }
    }

    data class Direction(
        val up: Coors = Coors(-1, 0),
        val down: Coors = Coors(1, 0),
        val left: Coors = Coors(0, -1),
        val right: Coors = Coors(0, 1)
    )

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    val pipes = mapOf(
        'S' to listOf(Direction().up, Direction().down), //my input
        '|' to listOf(Direction().up, Direction().down),
        '-' to listOf(Direction().left, Direction().right),
        'L' to listOf(Direction().up, Direction().right),
        'J' to listOf(Direction().up, Direction().left),
        '7' to listOf(Direction().down, Direction().left),
        'F' to listOf(Direction().down, Direction().right)
    )

    fun main() {
        val input = readFileAsList("input.txt")
        var start = Coors(0, 0)
        (input.indices).forEach { i ->
            (0 until input[i].length).forEach { j ->
                if (input[i][j] == 'S')
                    start = Coors(i, j)
            }
        }
        val startNeighbours = mutableListOf<Coors>()
        listOf(
            Pair(Direction().up, "|7F"),
            Pair(Direction().down, "|LJ"),
            Pair(Direction().left, "-LF"),
            Pair(Direction().right, "-J7")
        )
            .forEach { (margin, possibleNeighbours) ->
                val neighbour = start + margin
                if (neighbour.exists(
                        Coors(
                            input.size,
                            input[0].length
                        )
                    ) && input[neighbour.row][neighbour.col] in possibleNeighbours
                ) {
                    startNeighbours.add(neighbour)
                }
            }
        val loop = mutableListOf(start, startNeighbours.first())
        while (loop.last() != startNeighbours.last()) {
            val coors = loop.last()
            pipes[input[coors.row][coors.col]]!!.map { it + coors }.forEach { if (it !in loop) loop.add(it) }
        }
        //firstStar
        println(loop.size / 2)
        //secondStar
        println(secondStar(input, loop))
    }

    fun secondStar(input: List<String>, loop: MutableList<Coors>): Int {
        val matrix = MutableList(input.size * 2) { MutableList(input[0].length * 2) { '.' } }
        loop.forEach {
            matrix[it.row * 2][it.col * 2] = input[it.row][it.col]
            pipes[input[it.row][it.col]]!!
                .forEach { margin ->
                    if (margin == Direction().down)
                        matrix[it.row * 2 + margin.row][it.col * 2 + margin.col] = '|'
                    if (margin == Direction().right)
                        matrix[it.row * 2 + margin.row][it.col * 2 + margin.col] = '-'
                }
        }
        floodThePlace(matrix).forEach { matrix[it.row][it.col] = ' ' }
        val tiles = mutableListOf<Coors>()
        (0 until matrix.size - 1).forEach { row ->
            (0 until matrix[row].size - 1).forEach { col ->
                val square = listOf(
                    Coors(row, col),
                    Coors(row, col) + Direction().down,
                    Coors(row, col) + Direction().right,
                    Coors(row, col) + Coors(1, 1)
                )
                if (square.map { matrix[it.row][it.col] }.distinct() == listOf('.') && square.map { it in tiles }
                        .distinct() == listOf(false))
                    tiles.addAll(square)
            }
        }
        return tiles.size / 4
    }

    fun floodThePlace(input: MutableList<MutableList<Char>>): MutableList<Coors> {
        val directions = listOf(Direction().up, Direction().down, Direction().left, Direction().right)
        val notTiles = mutableListOf<Coors>()
        val queue = mutableListOf(
            Coors(0, 0),
            Coors(0, input[0].size - 1),
            Coors(input.size - 1, 0),
            Coors(input.size - 1, input[0].size - 1)
        )
        while (queue.isNotEmpty()) {
            queue.first().let { coors ->
                if (coors !in notTiles) {
                    notTiles.add(coors)
                    directions.map { margin ->
                        val new = coors + margin
                        if (new.exists(Coors(input.size, input[0].size))) {
                            if (input[new.row][new.col] == '.')
                                queue.add(coors + margin)
                        }
                    }
                }
            }
            queue.removeFirst()
        }
        return notTiles
    }
}
