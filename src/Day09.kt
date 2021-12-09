import java.util.Collections.min

fun main() {
    fun part1(lines: List<String>): Int {
        val map   = lines.map { line -> line.map { it.toString().toInt() } }
        var count = 0

        for (y in 0..map.lastIndex) {
            for (x in 0..map[0].lastIndex) {
                val neighbors = emptyList<Int>().toMutableList()

                if (x > 0)
                    neighbors += map[y][x - 1]
                if (x < map[0].lastIndex)
                    neighbors += map[y][x + 1]
                if (y > 0)
                    neighbors += map[y - 1][x]
                if (y < map.lastIndex)
                    neighbors += map[y + 1][x]

                if (min(neighbors) > map[y][x])
                    count += map[y][x] + 1
            }
        }
        return count
    }

    fun flood(
        map: MutableList<MutableList<Boolean>>,
        x: Int,
        y: Int
    ): Int {
        if (map[y][x])
            return 0

        map[y][x] = true

        var count = 1

        if (x > 0)
            count += flood(map, x - 1, y)
        if (x < map[0].lastIndex)
            count += flood(map, x + 1, y)
        if (y > 0)
            count += flood(map, x, y - 1)
        if (y < map.lastIndex)
            count += flood(map, x, y + 1)

        return count
    }

    fun part2(lines: List<String>): Int {
        val map = lines.map {
            line -> line.map { it == '9' }.toMutableList()
        }.toMutableList()
        val bassins = emptyList<Int>().toMutableList()

        for (y in 0..map.lastIndex) {
            for (x in 0..map[0].lastIndex) {
                if (!map[y][x])
                    bassins += flood(map, x, y)
            }
        }
        return bassins
            .sortedDescending()
            .take(3)
            .reduce { prod, n -> prod * n }
    }

    val testInput = readInput("Day09_test")
    expect(part1(testInput), 15)
    expect(part2(testInput), 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
