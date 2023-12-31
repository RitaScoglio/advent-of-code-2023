import java.io.File

class Day19 {

    data class Rule(val category: String, val bound: Int, val compare: String, val next: String)

    fun readFileAsString(fileName: String): String = File(fileName).readText(Charsets.UTF_8)

    fun main() {
        val input = readFileAsString("input.txt").split("\n\n")
        val (workflows, ratings) = parseInput(input)
        println(firstStar(ratings, workflows))
        println(secondStar(workflows))
    }

    private fun parseInput(input: List<String>) =
        Pair(input[0].lines().associate { line ->
            val (name, rules) = line.dropLast(1).split("{").map { it.split(",") }
            name[0] to rules.map { rule ->
                val condition = rule.split(":", "<", ">")
                if (condition.size == 1)
                    Rule("xmas", 0, ">", condition[0])
                else
                    Rule(condition[0], condition[1].toInt(), rule.filter { it == '<' || it == '>' }, condition[2])
            }
        },
            input[1].lines().map { line ->
                val (x, m, a, s) = line.drop(1).dropLast(1).split(",")
                    .map { cat -> cat.filter { it.isDigit() }.toInt() }
                mapOf("x" to x, "m" to m, "a" to a, "s" to s)
            })

    fun firstStar(ratings: List<Map<String, Int>>, workflows: Map<String, List<Rule>>) =
        ratings.associateWith { rating ->
            var workflow = "in"
            while (workflow != "A" && workflow != "R") {
                workflow = workflows[workflow]!!.first { compareParts(rating, it) }.next
            }
            workflow
        }.filter { it.value == "A" }.keys.sumOf { rating -> rating.values.sum() }

    fun compareParts(part: Map<String, Int>, condition: Rule): Boolean =
        if (condition.category == "xmas")
            true
        else if (condition.compare == "<")
            part[condition.category]!! < condition.bound
        else part[condition.category]!! > condition.bound

    fun secondStar(workflows: Map<String, List<Rule>>): Long {
        val accepted = mutableListOf<Map<String, IntRange>>()
        val partList = mutableListOf<Pair<String, Map<String, IntRange>>>()
        partList.add(Pair("in", mapOf("x" to 1..4000, "m" to 1..4000, "a" to 1..4000, "s" to 1..4000)))
        while (partList.isNotEmpty()) {
            var (workflow, part) = partList.first()
            partList.removeFirst()
            val conditions = workflows[workflow]!!
            conditions.forEach { condition ->
                if (part.keys.none { it == "" }) {
                    val (correct, incorrect) = splitByCondition(condition, part)
                    if (correct.keys.none { it == "" }) {
                        if (condition.next == "A")
                            accepted.add(correct)
                        else if (condition.next != "R")
                            partList.add(Pair(condition.next, correct))
                    }
                    part = incorrect
                }
            }
        }
        return accepted.sumOf { part ->
            part.map { (_, value) -> (value.last - value.first + 1).toLong() }.fold(1L) { acc, e -> acc * e }
        }
    }

    fun splitByCondition(
        condition: Rule,
        part: Map<String, IntRange>
    ): Pair<Map<String, IntRange>, Map<String, IntRange>> {
        if (condition.category == "xmas")
            return Pair(part, mapOf("" to 0..1))
        else if (condition.compare == "<" && part[condition.category]!!.first < condition.bound) {
            val mutablePart = part.toMutableMap()
            mutablePart[condition.category] = part[condition.category]!!.first until condition.bound
            val newAcceptPart = mutablePart.toMap()
            mutablePart[condition.category] = condition.bound..part[condition.category]!!.last
            val newRejectPart = mutablePart.toMap()
            return Pair(newAcceptPart, newRejectPart)
        } else if (condition.compare == ">" && part[condition.category]!!.last > condition.bound) {
            val mutablePart = part.toMutableMap()
            mutablePart[condition.category] = part[condition.category]!!.first..condition.bound
            val newRejectPart = mutablePart.toMap()
            mutablePart[condition.category] = condition.bound + 1..part[condition.category]!!.last
            val newAcceptPart = mutablePart.toMap()
            return Pair(newAcceptPart, newRejectPart)
        } else return Pair(mapOf("" to 0..1), part)
    }
}