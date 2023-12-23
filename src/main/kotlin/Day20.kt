import java.io.File

class Day20 {

    data class Module(val name: String, val type: Char, val sendTo: List<String>) {
        val latestPulses = mutableMapOf<String, String>()
        var on = false
    }

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val modules = readFileAsList("input.txt").map {
            val (name, sendTo) = it.replace(" ", "").split("->")
            when (name.first()) {
                '%', '&' -> Module(name.drop(1), name.first(), sendTo.split(","))
                else -> Module(name, 'b', sendTo.split(","))
            }
        }.associateBy { it.name }
        modules.values.filter { it.type == '%' }.forEach { module ->
            module.sendTo.forEach {
                if (modules[it]!!.type == '&')
                    modules[it]!!.latestPulses[module.name] = "low"
            }
        }
        println(firstStar(modules))
        println(secondStar(modules))
    }

    fun firstStar(modules: Map<String, Module>): Int {
        var buttonPush = 0
        val pulses = mutableMapOf("low" to 0, "high" to 0)
        do {
            buttonPush += 1
            simulatePulses(modules, pulses)
        } while (buttonPush < 1000)
        return (pulses["low"]!! * pulses["high"]!!)
    }

    fun secondStar(modules: Map<String, Module>): Long {
        val important = listOf("lr", "tf", "hl", "sn")
        val optimal = MutableList(important.size) { 0 }
        var buttonPush = 0
        val pulses = mutableMapOf("low" to 0, "high" to 0)
        do {
            buttonPush += 1
            simulatePulses(modules, pulses)
            important.indices.forEach { module ->
                if (modules[important[module]]!!.latestPulses.values.none { it == "high" } && buttonPush % 2 == 1 && optimal[module] == 0)
                    optimal[module] = buttonPush
            }
        } while (optimal.any { it == 0 })
        return optimal.fold(1L) { acc, e -> findLCM(acc, e.toLong()) }
    }

    fun simulatePulses(modules: Map<String, Module>, pulses: MutableMap<String, Int>) {
        pulses["low"] = pulses["low"]!! + 1
        val queue = mutableListOf<Triple<String, String, String>>()
        queue.addAll(modules["broadcaster"]!!.sendTo.map { Triple("low", "broadcaster", it) })
        while (queue.isNotEmpty()) {
            val (pulse, from, moduleName) = queue.first()
            queue.removeFirst()
            pulses[pulse] = pulses[pulse]!! + 1
            if (moduleName != "rx") {
                val module = modules[moduleName]!!
                when (module.type) {
                    '%' -> if (pulse == "low") {
                        if (modules[module.name]!!.on)
                            queue.addAll(module.sendTo.map { Triple("low", module.name, it) })
                        else
                            queue.addAll(module.sendTo.map { Triple("high", module.name, it) })
                        modules[module.name]!!.on = !module.on
                    }
                    '&' -> {
                        modules[module.name]!!.latestPulses[from] = pulse
                        if (modules[module.name]!!.latestPulses.values.all { it == "high" })
                            queue.addAll(module.sendTo.map { Triple("low", module.name, it) })
                        else
                            queue.addAll(module.sendTo.map { Triple("high", module.name, it) })
                    }
                }
            }
        }
    }

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