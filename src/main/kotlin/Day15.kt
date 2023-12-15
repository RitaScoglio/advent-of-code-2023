import java.io.File

class Day15 {
    data class Lens(val name: String, val focus: Int)

    fun readFileAsString(fileName: String): String = File(fileName).readText(Charsets.UTF_8)

    fun main() {
        val input = readFileAsString("input.txt").replace("\n", "").split(',')
        println(firstStar(input))
        println(secondStar(input))
    }

    fun firstStar(input: List<String>) =
        input.sumOf { string -> string.fold(0.toInt()) { acc, e -> ((acc + e.code) * 17) % 256 } }

    fun secondStar(input: List<String>): Int {
        val hashmap = mutableMapOf<Int, List<Lens>>()
        input.forEach { string ->
            val lens = string.split('=', '-')
            val box = lens[0].fold(0) { acc, e -> ((acc + e.code) * 17) % 256 }
            if (hashmap[box] == null) {
                if (lens[1] != "")
                    hashmap[box] = listOf(Lens(lens[0], lens[1].toInt()))
            } else {
                if (lens[1] != "")
                    if (hashmap[box]!!.map { it.name }.contains(lens[0]))
                        hashmap[box] = hashmap[box]!!.map { if (it.name == lens[0]) Lens(lens[0], lens[1].toInt()) else it }
                    else
                        hashmap.merge(box, listOf(Lens(lens[0], lens[1].toInt()))) { new, old -> new + old }
                else
                    hashmap[box] = hashmap[box]!!.filter { it.name != lens[0] }
            }
        }
        return hashmap.keys.sumOf { box ->
            hashmap[box]!!.indices.sumOf { index ->
                (box + 1) * (index + 1) * hashmap[box]!![index].focus
            }
        }
    }
}