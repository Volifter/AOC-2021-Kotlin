import kotlin.math.max

fun main() {
    data class Point(var x: Int, var y: Int)

    data class Fold(var isX: Boolean, var value: Int)

    fun parseLines(lines: List<String>): Pair<List<Point>, List<Fold>> {
        val points = emptyList<Point>().toMutableList()
        val folds  = emptyList<Fold>().toMutableList()

        lines.map { line ->
            if (line.isEmpty())
                return@map

            if (line.startsWith("fold along ")) {
                val (axis, value) = line.split("=")

                folds += Fold(axis == "fold along x", value.toInt())

                return@map
            }

            val (a, b) = line.split(",").map { it.toInt() }

            points += Point(a, b)
        }

        return Pair(points, folds)
    }

    fun foldPoints(pointsList: List<Point>, folds: List<Fold>): Set<Point> {
        var points = pointsList.toSet()

        folds.forEach { fold ->
            points.forEach { point ->
                if (fold.isX && point.x > fold.value)
                    point.x = 2 * fold.value - point.x
                if (!fold.isX && point.y > fold.value)
                    point.y = 2 * fold.value - point.y
            }
            points = points.toSet()
        }
        return points
    }

    fun part1(lines: List<String>): Int {
        val (points, folds) = parseLines(lines)

        return foldPoints(points, listOf(folds[0])).size
    }

    fun part2(lines: List<String>): String {
        val (points, folds) = parseLines(lines)
        val foldedPoints = foldPoints(points, folds)
        val (w, h) = foldedPoints.reduce { acc, point ->
            Point(max(acc.x, point.x), max(acc.y, point.y))
        }

        return (0..h).joinToString("\n") { y ->
            (0..w).joinToString("") { x ->
                if (Point(x, y) in foldedPoints) "#" else " "
            }
        }
    }

    val testInput = readInput("Day13_test")
    expect(part1(testInput), 17)
    expect(part2(testInput), listOf(
        "#####",
        "#   #",
        "#   #",
        "#   #",
        "#####"
    ).joinToString("\n"))

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
