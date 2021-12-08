fun main() {
    fun part1(lines: List<String>, duration: Int): Long {
        val fishes = lines[0].split(",").map { it.toInt() }.toMutableList()
        var counts = mapOf(*IntRange(0, 8).map { n ->
            Pair(n, fishes.count { it == n }.toLong())
        }.toTypedArray())

        for (i in 0 until duration) {
            counts = mapOf(*IntRange(0, 8).map {
                Pair(
                    it,
                    if (it == 6)
                        (counts[7]!! + counts[0]!!)
                    else counts[
                        if (it == 8) 0 else it + 1
                    ]!!
                )
            }.toTypedArray())
        }
        return counts.values.sum()
    }

    val testInput = readInput("Day06_test")
    expect(part1(testInput, 18), 26)
    expect(part1(testInput, 80), 5934)
    expect(part1(testInput, 256), 26984457539)

    val input = readInput("Day06")
    println(part1(input, 18))
    println(part1(input, 256))
}
