fun main() {
    fun part1(lines: List<String>): Int {
        var distance = 0
        var depth    = 0

        lines.forEach {
            val (a, b) = it.split(" ")
            val n = b.toInt()

            when (a[0]) {
                'f' -> distance += n
                'd' -> depth += n
                'u' -> depth -= n
            }
        }
        return distance * depth
    }

    fun part2(lines: List<String>): Int {
        var aim      = 0
        var distance = 0
        var depth    = 0

        lines.forEach {
            val (a, b) = it.split(" ")
            val n = b.toInt()

            when (a[0]) {
                'f' -> {
                    distance += n
                    depth += aim * n
                }
                'd' -> aim += n
                'u' -> aim -= n
            }
        }
        return distance * depth
    }

    val testInput = readInput("Day02_test")
    expect(part1(testInput), 150)
    expect(part2(testInput), 900)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
