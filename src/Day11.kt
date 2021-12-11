fun main() {
    fun parseLines(
        lines: List<String>
    ): Pair<MutableList<MutableList<Int>>, List<Pair<Int, Int>>> {
        val map = lines.map {
                line -> line.map { it.digitToInt() }.toMutableList()
        }.toMutableList()
        val coordinates = (0 until map.size * map[0].size).map {
            Pair(it % map[0].size, it / map.size)
        }

        return Pair(map, coordinates)
    }

    fun spread(map: MutableList<MutableList<Int>>, x: Int, y: Int): Int {
        if (map[y][x] < 10)
            return 0

        var count = 1

        map[y][x] = 0

        for (newX in x - 1..x + 1) {
            for (newY in y - 1..y + 1) {
                if (
                    newX in 0..map[0].lastIndex
                    && newY in 0..map.lastIndex
                    && map[newY][newX] != 0
                ) {
                    map[newY][newX]++
                    count += spread(map, newX, newY)
                }
            }
        }

        return count
    }

    fun part1(lines: List<String>, cycles: Int): Int {
        val (map, coordinates) = parseLines(lines)
        var flashes = 0

        for (i in 0 until cycles) {
            coordinates.forEach { (x, y) -> map[y][x]++ }
            coordinates.forEach { (x, y) -> flashes += spread(map, x, y) }
        }
        return flashes
    }

    fun part2(lines: List<String>): Int {
        val (map, coordinates) = parseLines(lines)
        var i = 0

        while (
            coordinates.sumOf { (x, y) ->
                spread(map, x, y)
            } != map.size * map[0].size
        ) {
            coordinates.forEach { (x, y) -> map[y][x]++ }
            i++
        }
        return i
    }

    val testInput = readInput("Day11_test")
    expect(part1(testInput, 100), 1656)
    expect(part2(testInput), 195)

    val input = readInput("Day11")
    println(part1(input, 100))
    println(part2(input))
}
