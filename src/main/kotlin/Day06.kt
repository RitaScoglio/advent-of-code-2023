import java.io.File

class Day06 {

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt").map { line ->
            line.substring(line.indexOf(':') + 1)
                .replace("  ", " ")
                .split(' ').filter { it != "" }.map { it.toLong() }
        }
        //firstStar
        println(countPossibilities(input))
        //secondStar
        println(countPossibilities(input.map { listOf(it.joinToString("").toLong()) }))
    }

    private fun countPossibilities(input: List<List<Long>>): Long {
        val possibilities = MutableList(input[0].size) { 0.toLong() }
        input[0].indices.forEach { index ->
            possibilities[index] = (0..input[0][index]).map { holding ->
                (input[0][index] - holding) * holding
            }.filter { it > input[1][index] }.size.toLong()
        }
        return possibilities.fold(1.toLong()) { acc, e -> acc * e }
    }
}