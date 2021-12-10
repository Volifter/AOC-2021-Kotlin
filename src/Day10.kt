val BRACKET_PAIRS = mapOf(
    Pair('(', ')'),
    Pair('[', ']'),
    Pair('{', '}'),
    Pair('<', '>')
)

enum class Mode { SYNTAX_ERROR, COMPLETION }

fun main() {
    fun parseBrackets(line: String, mode: Mode): String {
        val stack = emptyList<Char>().toMutableList()

        line.forEach {
            val pair = BRACKET_PAIRS[it]

            if (pair == null) {
                val last = stack.removeLast()

                if (last != it)
                    return if (mode == Mode.SYNTAX_ERROR) it.toString() else ""
            } else {
                stack += pair
            }
        }

        return if (mode == Mode.COMPLETION) stack.reversed().joinToString("")
            else ""
    }

    fun part1(lines: List<String>): Int {
        val scores = mapOf(
            Pair(')', 3),
            Pair(']', 57),
            Pair('}', 1197),
            Pair('>', 25137)
        )
        var score = 0

        lines.forEach { line ->
            val result = parseBrackets(line, Mode.SYNTAX_ERROR)

            if (result.isNotEmpty())
                score += scores[result[0]]!!
        }

        return score
    }

    fun part2(lines: List<String>): Long {
        val order  = ")]}>"
        val scores = emptyList<Long>().toMutableList()

        lines.forEach lines@{ line ->
            val score = parseBrackets(line, Mode.COMPLETION)
                .fold(0L) { score, c -> score * 5 + order.indexOf(c) + 1 }

            if (score != 0L)
                scores += score
        }

        return scores.sorted()[scores.size / 2]
    }

    val testInput = readInput("Day10_test")
    expect(part1(testInput), 26397)
    expect(part2(testInput), 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
