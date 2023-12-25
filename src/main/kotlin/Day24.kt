import java.io.File
import kotlin.math.sign


class Day24 {

    data class Line(val x: Double, val y: Double, val z: Double, val x_v: Double, val y_v: Double, val z_v: Double)

    fun readFileAsList(fileName: String): List<String> = File(fileName).useLines { it.toList() }

    fun main() {
        val lines = readFileAsList("input.txt").map { line ->
            line.replace(" ", "").split("@", ",")
                .let {
                    Line(it[0].toDouble(), it[1].toDouble(), it[2].toDouble(), it[3].toDouble(), it[4].toDouble(), it[5].toDouble())
                }
        }
        println(firstStar(lines, 200000000000000.0..400000000000000.0))
        //secondStar => python +z3
    }

    fun firstStar(lines: List<Line>, bound: ClosedFloatingPointRange<Double>) =
        lines.indices.sumOf { i ->
            (i until lines.size).map { j ->
                val det = (lines[i].x_v * -lines[j].y_v) + (lines[j].x_v * lines[i].y_v)
                if (det != 0.0) {
                    val det1 =
                        (-lines[j].y_v * (-lines[i].x + lines[j].x) + lines[j].x_v * (-lines[i].y + lines[j].y)) / det
                    val intersection = Pair(lines[i].x + det1 * lines[i].x_v, lines[i].y + det1 * lines[i].y_v)
                    if (intersection.first in bound && intersection.second in bound &&
                        isInFuture(intersection, listOf(lines[i], lines[j])))
                        1
                    else 0
                } else 0
            }.sum() }

    fun isInFuture(intersection: Pair<Double, Double>, lines: List<Line>) =
        lines.fold(true) { acc, line -> acc && (sign(line.x_v) == sign(intersection.first - line.x)) &&
                (sign(line.y_v) ==  sign(intersection.second - line.y)) }
}