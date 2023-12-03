import java.io.File

class Day02 {

    data class Tricolor(var red: Int, var green: Int, var blue: Int) {

        operator fun compareTo(bound: Tricolor): Int {
            return maxOf(red - bound.red, green - bound.green, blue - bound.blue)
        }

        fun merge(other: Tricolor) {
            this.red += other.red
            this.green += other.green
            this.blue += other.blue
        }
    }

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
        val allGames = mutableMapOf<Int, List<Tricolor>>()
        input.indices.forEach { game ->
            val rounds = input[game].substring(input[game].indexOf(":") + 1, input[game].length)
                    .filter { it != ' ' }
                    .split(";")
            val listOfRounds = mutableListOf<Tricolor>()
            rounds.forEach { round ->
                val tricolor = Tricolor(0, 0, 0)
                round.split(',').forEach { tricolor.merge(extractNumberAndColor(it)) }
                listOfRounds.add(tricolor)
            }
            allGames[game + 1] = listOfRounds
        }
        println(firstStar(allGames))
        println(secondStar(allGames))
    }

    fun firstStar(allGames: MutableMap<Int, List<Tricolor>>): Int {
        val bound = Tricolor(12, 13, 14)
        return allGames.keys.sumOf { key -> if (overBound(allGames.getValue(key), bound)) 0 else key }
    }

    fun secondStar(allGames: MutableMap<Int, List<Tricolor>>) = allGames.keys.sumOf { key ->
        minimumCubes(allGames.getValue(key)) }

    fun extractNumberAndColor(cubes: String): Tricolor {
        val number = cubes.filter { it.isDigit() }
        return when (cubes.substring(number.length)) {
            "red" -> Tricolor(number.toInt(), 0, 0)
            "green" -> Tricolor(0, number.toInt(), 0)
            "blue" -> Tricolor(0, 0, number.toInt())
            else -> Tricolor(0, 0, 0)
        }
    }

    fun overBound(triples: List<Tricolor>, bound: Tricolor): Boolean = triples.map { it > bound }.contains(true)

    fun minimumCubes(values: List<Tricolor>): Int =
        values.map { it.red }.max() * values.map { it.green }.max() * values.map { it.blue }.max()
}

