import java.io.File
import kotlin.math.pow

class Day04 {
    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val input = readFileAsList("input.txt")
        val cards = mutableMapOf<Int, Int>()
        input.indices.forEach { card ->
            input[card].substring(input[card].indexOf(":") + 1, input[card].length)
                .replace("  ", " ")
                .split('|')
                .map { it.split(' ').filter { it != "" }.map { it.toInt() } }
                .let { (winning, mine) ->
                    cards.put(card + 1, winning.map { if (it in mine) 1 else 0 }.sum())
                }
        }
        println(firstStar(cards))
        println(secondStar(cards))
    }

    fun firstStar(cards: MutableMap<Int, Int>) = cards.values.map { if (it > 0) 2.0.pow(it - 1).toInt() else 0 }.sum()

    fun secondStar(cards: MutableMap<Int, Int>): Int {
        val numberOfCards = MutableList(cards.size) { 1 }
        cards.forEach { (card, wins) -> ((card + 1)..(card + wins))
            .forEach { new -> numberOfCards[new] += numberOfCards[card] } }
        return numberOfCards.sum()
    }
}