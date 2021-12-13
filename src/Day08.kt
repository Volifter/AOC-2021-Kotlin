fun main() {
    val SEGMENT_COUNTS = mapOf(
        Pair(4, "e".toSet()),
        Pair(6, "b".toSet()),
        Pair(7, "dg".toSet()),
        Pair(8, "ac".toSet()),
        Pair(9, "f".toSet()),
    )
    val DIGIT_SEQUENCES = mapOf(
        Pair(0, "abcefg".toSet()),
        Pair(1, "cf".toSet()),
        Pair(2, "acdeg".toSet()),
        Pair(3, "acdfg".toSet()),
        Pair(4, "bcdf".toSet()),
        Pair(5, "abdfg".toSet()),
        Pair(6, "abdefg".toSet()),
        Pair(7, "acf".toSet()),
        Pair(8, "abcdefg".toSet()),
        Pair(9, "abcdfg".toSet())
    )
    val SEQUENCE_DIGITS = mapOf(*DIGIT_SEQUENCES.map {
        Pair(it.value, it.key)
    }.toTypedArray())

    fun compute(
        sequences: Set<Set<Char>>,
        segments: Set<Char>,
        segmentMap: Map<Char, Char>
    ): Map<Char, Char>? {
        if (segments.isEmpty()) {
            if (sequences.any {
                seq -> !SEQUENCE_DIGITS.contains(
                    seq.map { segmentMap[it] }.toSet()
                )
            })
                return null
            return segmentMap
        }

        val segmentFrom = segments.first()
        val count = sequences.count { it.contains(segmentFrom) }

        SEGMENT_COUNTS[count]!!.forEach { segmentTo ->
            if (segmentMap.containsValue(segmentTo))
                return@forEach

            val result = compute(
                sequences,
                segments - segmentFrom,
                segmentMap + mapOf(Pair(segmentFrom, segmentTo)),
            )

            if (result != null)
                return result
        }
        return null
    }

    fun parseLine(line: String): List<List<String>> {
        return line.split(" | ").map { part ->
            part.split(" ").map {
                it.toCharArray().sorted().joinToString("")
            }
        }
    }

    fun getDigits(sequences: List<String>, digits: List<String>): List<Int> {
        val map = compute(
            sequences.map { it.toSet() }.toSet(),
            ('a'..'g').toSet(),
            emptyMap()
        )!!

        return digits.map {
                seq -> SEQUENCE_DIGITS[seq.map { map[it] }.toSet()]!!
        }
    }

    fun part1(lines: List<String>): Int {
        return lines.sumOf { line ->
            val (sequences, digits) = parseLine(line)

            getDigits(sequences, digits).count {
                setOf(1, 4, 7, 8).contains(it)
            }
        }
    }

    fun part2(lines: List<String>): Int {
        return lines.sumOf { line ->
            val (sequences, digits) = parseLine(line)

            getDigits(sequences, digits).reduce { a, b -> a * 10 + b }
        }
    }

    val testInput = readInput("Day08_test")
    expect(part1(testInput), 26)
    expect(part2(testInput), 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
