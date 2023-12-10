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

    fun main() {
        val input = readFileAsList("input.txt")
        var start = Coors(0, 0)
        (input.indices).forEach { i ->
            (0 until input[i].length).forEach { j ->
                if (input[i][j] == 'S')
                    start = Coors(i, j)
            }
        }
        val pipes = mapOf(
            '|' to listOf(Direction().up, Direction().down),
            '-' to listOf(Direction().left, Direction().right),
            'L' to listOf(Direction().up, Direction().right),
            'J' to listOf(Direction().up, Direction().left),
            '7' to listOf(Direction().down, Direction().left),
            'F' to listOf(Direction().down, Direction().right)
        )
        val startNeighbours = mutableListOf<Coors>()
        listOf(
            Pair(Direction().up, "|7F"),
            Pair(Direction().down, "|LJ"),
            Pair(Direction().left, "-LF"),
            Pair(Direction().right, "-J7")
        ).forEach { (margin, possibleNeighbours) ->
                val neighbour = start + margin
                if (neighbour.exists(Coors(input.size, input[0].length))
                    && input[neighbour.row][neighbour.col] in possibleNeighbours)
                    startNeighbours.add(neighbour) }
        val loop = mutableListOf(start, startNeighbours.first())
        while (loop.last() != startNeighbours.last()) {
            val coors = loop.last()
            pipes[input[coors.row][coors.col]]!!.map { it + coors }.forEach { if (it !in loop) loop.add(it) }
        }
        //firstStar
        println(loop.size / 2)
        //secondStar
        println(secondStar(loop + loop.first(), loop.size / 2)) //Pick's theorem + shoelace formula
    }

    private fun secondStar(loop: List<Coors>, halfCycle: Int) =
        (loop.windowed(2, 1).sumOf { (coors1, coors2) ->
            (coors1.row * coors2.col) - (coors1.col * coors2.row)
        } / 2) - halfCycle + 1
}
