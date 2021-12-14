fun main() {
    fun getPairs(sequence: String): Map<Pair<Char, Char>, Long> {
        val pairs = sequence.zipWithNext()

        return pairs.toSet().associateWith {
            pair -> pairs.count { it == pair}.toLong()
        }
    }

    fun inflatePairs(
        initial_pairs: Map<Pair<Char, Char>, Long>,
        polymers: Map<Pair<Char, Char>, Char>,
        n: Int
    ): Map<Pair<Char, Char>, Long> {
        var pairs = initial_pairs

        (0 until n).forEach { _ ->
            val new_pairs = emptyMap<Pair<Char, Char>, Long>().toMutableMap()

            pairs.forEach { (pair, frequency) ->
                val c     = polymers[pair]!!
                val left  = Pair(pair.first, c)
                val right = Pair(c, pair.second)

                if (left !in new_pairs)
                    new_pairs[left] = 0
                if (right !in new_pairs)
                    new_pairs[right] = 0

                new_pairs[left]  = new_pairs[left]!! + frequency
                new_pairs[right] = new_pairs[right]!! + frequency
            }
            pairs = new_pairs
        }
        return pairs
    }

    fun countCharFrequencies(
        pairs: Map<Pair<Char, Char>, Long>
    ): List<Pair<Char, Long>> {
        val frequencies = emptyMap<Char, Long>().toMutableMap()

        pairs.forEach {(pair, frequency) ->
            pair.toList().forEach { c ->
                if (c !in frequencies)
                    frequencies[c] = 0
                frequencies[c] = frequencies[c]!! + frequency
            }
        }

        return frequencies.toList().map { (c, frequency) ->
            Pair(c, frequency / 2 + (frequency and 1))
        }.sortedBy { it.second }
    }

    fun part1(lines: List<String>, n: Int): Long {
        val sequence        = lines[0]
        val polymers        = lines.drop(2).associate {
            Pair(Pair(it[0], it[1]), it.last())
        }
        val pairs           = getPairs(sequence)
        val pairFrequencies = inflatePairs(pairs, polymers, n)
        val charFrequencies = countCharFrequencies(pairFrequencies)

        return charFrequencies.last().second - charFrequencies[0].second
    }

    val testInput = readInput("Day14_test")
    expect(part1(testInput, 10), 1588)
    expect(part1(testInput, 40), 2188189693529)

    val input = readInput("Day14")
    println(part1(input, 10))
    println(part1(input, 40))
}
