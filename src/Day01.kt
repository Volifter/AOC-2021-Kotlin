fun main() {
    fun countRaising(values: List<Int>): Int {
        return values.zip(values.drop(1)).count { it.first < it.second }
    }

    fun part1(input: List<String>): Int {
        return countRaising(input.map { it.toInt() })
    }

    fun part2(input: List<String>): Int {
        val values = input.map { it.toInt() }
        val chunks = IntRange(0, values.size - 3).map {
            values.slice(IntRange(it, it + 2)).sum()
        }

        return countRaising(chunks)
    }

    val testInput = readInput("Day01_test")
    expect(part1(testInput), 7)
    expect(part2(testInput), 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
