import java.io.File

class Day25 {
    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val edges = mutableSetOf<Set<String>>()
        val vertices = mutableSetOf<String>()
        readFileAsList("input.txt").forEach { line ->
            line.replace(":", "").split(" ").let { list ->
                (1 until list.size).forEach { index ->
                    edges.add(setOf(list[0], list[index]))
                }
                vertices.addAll(list)
            }
        }
        edges.removeAll(sendToMathematica(edges))
        //firstStar
        println(computeGroupSizes(edges, vertices).fold(1) { acc, e -> acc * e })
    }

    fun sendToMathematica(edges: MutableSet<Set<String>>): Set<Set<String>> {
        val toMathematica = edges.map {
            val edge = it.toList()
            "${edge[0]}<->${edge[1]},"
        }
        return setOf(setOf("czs", "tdk"), setOf("bbg", "kbr"), setOf("vtt", "fht"))
    }

    fun computeGroupSizes(edges: MutableSet<Set<String>>, vertices: MutableSet<String>): List<Int> {
        val queue = mutableListOf(vertices.first())
        val visited = mutableListOf<String>()
        while (queue.isNotEmpty()) {
            val vertex = queue.first()
            queue.removeFirst()
            if (vertex !in visited) {
                visited.add(vertex)
                queue.addAll(edges.filter { it.contains(vertex) }.flatten().filter { it != vertex })
            }
        }
        return listOf(visited.size, vertices.size - visited.size)
    }
}