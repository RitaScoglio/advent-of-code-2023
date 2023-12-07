import java.io.File
import kotlin.reflect.KFunction1

class Day07 {

    data class Hand(val cards: List<Char>, val bid: Int)

    val DECK_part1 = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')

    val DECK_part2 = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
            .map { it.split(' ') }
            .map { hand -> Hand(hand[0].toList(), hand[1].toInt()) }
        //firstStar
        println(getWinnings(getTypesOfHands(input, ::occurrencesWithJ).values, DECK_part1))
        //secondStar
        println(getWinnings(getTypesOfHands(input, ::occurrencesWithoutJ).values, DECK_part2))
    }

    fun getTypesOfHands(
        input: List<Hand>,
        getOccurrences: KFunction1<List<Char>, MutableList<Int>>
    ): MutableMap<Int, List<Hand>> {
        val types = mutableMapOf<Int, List<Hand>>()
        input.forEach { hand ->
            val occurrences = getOccurrences(hand.cards)
            val key = when (occurrences.size) {
                1 -> 7
                2 -> { if (occurrences == listOf(3, 2)) 5 else 6 }
                3 -> { if (occurrences == listOf(2, 2, 1)) 3 else 4 }
                4 -> 2
                else -> 1
            }
            types.merge(key, listOf(hand)) { new, old -> new + old }
        }
        return types.toSortedMap()
    }

    fun occurrencesWithJ(cards: List<Char>) =
        cards.distinct().map { card -> cards.count { it == card } }.sortedDescending().toMutableList()

    fun occurrencesWithoutJ(cards: List<Char>): MutableList<Int> {
        val occurrenceOfJ = cards.count { it == 'J' }
        val occurrences =
            cards.filter { it != 'J' }.distinct().map { card -> cards.count { it == card } }.sortedDescending()
                .toMutableList()
        if (occurrences.size == 0)
            occurrences.add(occurrenceOfJ)
        else
            occurrences[0] += occurrenceOfJ
        return occurrences
    }

    fun getWinnings(values: MutableCollection<List<Hand>>, deck: List<Char>): Int {
        val ranking = mutableListOf<Int>()
        values.forEach { list ->
            list.toMutableList().let {
                it.sortWith(compareBy<Hand> { deck.indexOf(it.cards[0]) }
                    .thenBy { deck.indexOf(it.cards[1]) }
                    .thenBy { deck.indexOf(it.cards[2]) }
                    .thenBy { deck.indexOf(it.cards[3]) }
                    .thenBy { deck.indexOf(it.cards[4]) })
                it.reversed().forEach { hand -> ranking.add(hand.bid * (ranking.size + 1)) }
            }
        }
        return ranking.sum()
    }
}

