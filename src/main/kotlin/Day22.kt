import java.io.File

class Day22 {

    data class Brick(val x: IntRange, val y: IntRange, val z: IntRange) {
        fun IsOverlapping(other: Brick): Boolean {
            return this.x.intersect(other.x).isNotEmpty() &&
                    this.y.intersect(other.y).isNotEmpty()
        }
    }

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val bricks = readFileAsList("input.txt")
            .map { line ->
                line.split("~", ",")
                    .let {
                        Brick(
                            it[0].toInt()..it[3].toInt(),
                            it[1].toInt()..it[4].toInt(),
                            it[2].toInt()..it[5].toInt()
                        )
                    }
            }
            .sortedWith(compareBy<Brick> { it.z.first }).toMutableList()
        val queue = bricks.filter { it.z.first != 1 }.toMutableList()
        while (queue.isNotEmpty()) {
            val brick = queue.first()
            queue.removeFirst()
            if (brick.z.first > 1) {
                if (bricks.filter { it.z.last == brick.z.first - 1 }.map { brick.IsOverlapping(it) }.none { it }) {
                    val newBrick = Brick(brick.x, brick.y, brick.z.first - 1 until brick.z.last)
                    bricks.remove(brick)
                    bricks.add(newBrick)
                    queue.add(newBrick)
                }
            }
        }
        val fallSimulation = bricks.associateWith { brick ->
            removeAndMoveBricks(bricks.filter { it != brick }, brick.z.last)
        }
        println(firstStar(fallSimulation))
        println(secondStar(fallSimulation))
    }

    fun firstStar(simulation: Map<Brick, Int>) = simulation.filter { it.value == 0 }.size

    fun secondStar(simulation: Map<Brick, Int>) = simulation.filter { it.value != 0 }.values.sum()

    fun removeAndMoveBricks(bricks: List<Brick>, z: Int): Int {
        val stillStanding = bricks.toMutableList()
        bricks.filter { it.z.first > z }.forEach { brick ->
            if (brick.z.first > 1) {
                if (stillStanding.filter { it.z.last == brick.z.first - 1 }.map { brick.IsOverlapping(it) }
                        .none { it }) {
                    stillStanding.remove(brick)
                }
            }
        }
        return bricks.size - stillStanding.size
    }
}