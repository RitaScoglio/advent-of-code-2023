import java.io.File

class Day18 {
    data class Coors(val x: Long, val y: Long)

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
        //firstStar
        println(getArea(input, ::getDirectionAndSize1))
        //secondStar
        println(getArea(input, ::getDirectionAndSize2))
    }

    fun getArea(input: List<String>, function: (String) -> Pair<String, Long>): Long {
        var loopSize = 0L
        val loop = mutableListOf(Coors(0, 0))
        input.forEach { line ->
            val (direction, size) = function(line)
            loop.add(getNewCoors(loop.last(), direction, size))
            loopSize += size
        }
        return (loop.windowed(2, 1)
            .sumOf { (first, second) -> (first.x * second.y) - (first.y * second.x) } / 2) + 1 + (loopSize / 2)
    }

    fun getNewCoors(coors: Coors, direction: String, size: Long) = when (direction) {
        "U" -> Coors(coors.x, coors.y - size)
        "D" -> Coors(coors.x, coors.y + size)
        "L" -> Coors(coors.x - size, coors.y)
        else -> Coors(coors.x + size, coors.y)
    }

    fun getDirectionAndSize1(line: String): Pair<String, Long> {
        val (direction, size, _) = line.split(" ")
        return Pair(direction, size.toLong())
    }

    fun getDirectionAndSize2(line: String): Pair<String, Long> {
        val color = line.split(" ")[2].drop(2).dropLast(1)
        val size = color.take(5).toLong(radix = 16)
        val direction = when (color.takeLast(1)) {
            "0" -> "R"
            "1" -> "D"
            "2" -> "L"
            else -> "U"
        }
        return Pair(direction, size)
    }
}