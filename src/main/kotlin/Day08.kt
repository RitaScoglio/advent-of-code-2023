import java.io.File

class Day08 {
    data class Child(val left: String, val right: String)

    fun readFileAsString(fileName: String): String = File(fileName).readText(Charsets.UTF_8)

    fun main() {
        val input = readFileAsString("input.txt").replace("\n\n", "\n").split("\n")
        val instructions = input[0]
        val tree = mutableMapOf<String, Child>()
        (1 until input.size).forEach { index ->
            input[index]
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .split("=", ",").let { node -> tree.put(node[0], Child(node[1], node[2])) }
        }
        //firstStar
        println(getSteps("AAA", listOf("ZZZ"), instructions, tree))
        //secondStar
        val endingNodes = tree.keys.filter { it[2] == 'Z' }
        println(findLCMList(tree.keys.filter { it[2] == 'A' }
            .map { getSteps(it, endingNodes, instructions, tree).toLong() }))
    }

    private fun getSteps(
        startNode: String,
        endNodes: List<String>,
        instructions: String,
        tree: MutableMap<String, Child>
    ): Int {
        var steps = 0
        var currentNode = startNode
        while (currentNode !in endNodes) {
            val curr = tree[currentNode]!!
            currentNode = if (instructions[steps % instructions.length] == 'L')
                curr.left
            else curr.right
            steps += 1
        }
        return steps
    }

    fun findLCMList(numbers: List<Long>) =
        (1 until numbers.size).fold(numbers[0]) { acc, e -> findLCM(acc, numbers[e]) }

    fun findLCM(a: Long, b: Long): Long {
        val bigger = if (a > b) a else b
        val max = a * b
        var lcm = bigger
        while (lcm < max) {
            if (lcm % a == 0.toLong() && lcm % b == 0.toLong()) {
                return lcm
            }
            lcm += bigger
        }
        return max
    }
}
