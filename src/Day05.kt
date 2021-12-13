import kotlin.math.abs

fun main() {
    data class Point(val x: Int, val y: Int)

    fun addLine(
        a: Point,
        b: Point,
        points: MutableMap<Pair<Int, Int>, Int>
    ): Int {
        val xDir  = if (a.x == b.x) 0 else if (a.x < b.x) 1 else -1
        val yDir  = if (a.y == b.y) 0 else if (a.y < b.y) 1 else -1
        val delta = abs(if (xDir != 0) a.x - b.x else a.y - b.y)
        var count = 0

        for (step in 0..delta) {
            val x = a.x + xDir * step
            val y = a.y + yDir * step
            val point = Pair(x, y)

            points[point] = points.getOrDefault(point, 0) + 1
            if (points[point] == 2)
                count++
        }
        return count
    }

    fun countLines(lines: List<String>, countDiagonals: Boolean): Int {
        val points = mutableMapOf<Pair<Int, Int>, Int>()
        var count  = 0

        lines.forEach { line ->
            val (l, r)     = line.split(" -> ")
            val (a_x, a_y) = l.split(",").map { it.toInt() }
            val (b_x, b_y) = r.split(",").map { it.toInt() }
            val a = Point(a_x, a_y)
            val b = Point(b_x, b_y)

            if (countDiagonals || a.x == b.x || a.y == b.y)
                count += addLine(a, b, points)
        }
        return count
    }

    val part1 = { lines: List<String> -> countLines(lines, false) }

    val part2 = { lines: List<String> -> countLines(lines, true) }

    val testInput = readInput("Day05_test")
    expect(part1(testInput), 5)
    expect(part2(testInput), 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
