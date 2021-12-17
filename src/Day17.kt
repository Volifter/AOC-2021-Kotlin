import kotlin.math.*

fun main() {
    fun getRanges(line: String): Pair<IntRange, IntRange> {
        val regex = """target area: x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)"""
            .toRegex()
        val (x_start, x_end, y_start, y_end) = regex
            .find(line)!!
            .destructured
            .toList()
            .map { n -> n.toInt() }

        return Pair(
            (min(x_start, x_end)..max(x_start, x_end)),
            (min(y_start, y_end)..max(y_start, y_end))
        )
    }

    fun sumTo(n: Int): Int = n * (n + 1) / 2

    fun sumBetween(a: Int, b: Int): Int = sumTo(b) - sumTo(a)

    fun part1(line: String): Int {
        val (_, y_range) = getRanges(line)

        return sumTo(-y_range.first - 1)
    }

    fun part2(line: String): Int {
        val (x_range, y_range) = getRanges(line)

        return (y_range.first until -y_range.first).sumOf { y_velocity ->
            val abs_vel    = abs(y_velocity)
            val offset     = if (y_velocity > 0) y_velocity * 2 else 0
            val from_x     = offset + ceil(
                sqrt((abs_vel - .5).pow(2) - 2 * y_range.last) - abs_vel + .5
            ).toInt()
            val to_x       = offset + floor(
                sqrt((abs_vel - .5).pow(2) - 2 * y_range.first) - abs_vel + .5
            ).toInt()
            var range: IntRange? = null

            (from_x..to_x).forEach { x_velocity ->
                val filter = { start_x: Int ->
                    sumBetween(
                        start_x - min(start_x, x_velocity), start_x
                    ) in x_range
                }
                val from   = (1..x_range.last).first(filter)
                val to     = (1..x_range.last).last(filter)

                range = range?.let {
                    (min(from, it.first())..max(to, it.last()))
                } ?: (from..to)
            }
            return@sumOf range?.count() ?: 0
        }
    }

    val testInput = readInput("Day17_test")[0]
    expect(part1(testInput), 45)
    expect(part2(testInput), 112)

    val input = readInput("Day17")[0]
    println(part1(input))
    println(part2(input))
}
