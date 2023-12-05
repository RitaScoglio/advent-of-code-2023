import java.io.File

class Day05 {

    data class Range(val from: Long, val to: Long, val length: Long)

    fun readFileAsString(fileName: String): String = File(fileName).readText(Charsets.UTF_8)

    fun main() {
        val input = readFileAsString("input.txt").split("\n\n").map { it.substring(it.indexOf(':') + 2) }
        val seeds = input[0].split(' ').map { it.toLong() }
        val converters: List<List<Range>> = (1..input.size - 1).map { index ->
            input[index].split("\n")
                .map { range ->
                    range.split(' ').let {
                        Range(it[1].toLong(), it[0].toLong(), it[2].toLong())
                    }
                }
        }
        println(firstStar(seeds, converters))
        println(secondStar(seeds, converters))
    }

    fun firstStar(seeds: List<Long>, converters: List<List<Range>>) = seeds.map { seed ->
        var convertedSeed = seed
        converters.forEach { ranges ->
            ranges.firstOrNull { convertedSeed >= it.from && convertedSeed < it.from + it.length }.let { range ->
                if (range != null)
                    convertedSeed = range.to + convertedSeed - range.from
            }
        }
        convertedSeed
    }.min()

    fun secondStar(seeds: List<Long>, converters: List<List<Range>>): Int {
        //interval obtain on paper
        (51399228..1111558812).forEach { location ->
            var convertedLoc: Long = location.toLong()
            converters.reversed().forEach { ranges ->
                ranges.firstOrNull { convertedLoc >= it.to && convertedLoc < it.to + it.length }.let { range ->
                    if (range != null)
                        convertedLoc = range.from + convertedLoc - range.to
                }
            }
            if (seeds.chunked(2).firstOrNull { convertedLoc > it[0] && convertedLoc < (it[0] + it[1]) } != null) {
                return location
            }
        }
        return -1
    }
}