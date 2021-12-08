import java.lang.Integer.min
import java.util.Collections.max
import kotlin.math.abs

fun main() {
    fun part1(lines: List<String>): Int {
        val values = lines[0].split(",").map { it.toInt() }.toMutableList()
        val med = values.sorted()[values.size / 2]

        return values.sumOf { abs(it - med) }
    }

    fun part2(lines: List<String>): Int {
        val values = lines[0].split(",").map { it.toInt() }.toMutableList()
        var best = Int.MAX_VALUE

        for (n in 0..max(values)) {
            val cost = values.sumOf {
                val d = abs(it - n)
                d * (d + 1) / 2
            }
            best = min(best, cost)
        }
        return best
    }

    val testInput = readInput("Day07_test")
    expect(part1(testInput), 37)
    expect(part2(testInput), 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
