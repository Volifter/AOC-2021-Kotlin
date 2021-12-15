fun main() {
    class Pos(val x: Int, val y: Int) {
        operator fun plus(other: Pos): Pos {
            return Pos(this.x + other.x, this.y + other.y)
        }

        operator fun compareTo(other: Pos): Int {
            if (this.x != other.x)
                return this.x - other.x
            if (this.y != other.y)
                return this.y - other.y
            return 0
        }

        override operator fun equals(other: Any?): Boolean {
            return other is Pos
                && this.x == other.x
                && this.y == other.y
        }

        override fun hashCode(): Int {
            return 31 * this.x + this.y
        }

        override fun toString(): String {
            return "Pos(${this.x}, ${this.y})"
        }
    }

    fun getWeightsMap(lines: List<String>): List<List<Int>> {
        return lines.map { line -> line.map { it.toString().toInt() } }
    }

    fun inflateWeightsMap(
        map: List<List<Int>>,
        factor: Int = 5
    ): List<List<Int>> {
        val (w, h) = Pair(map[0].size, map.size)

        return (0 until h * factor).map { y ->
            (0 until w * factor).map { x ->
                1 + (map[y % h][x % w] + x / w + y / h - 1) % 9
            }
        }
    }

    fun getVisitedMap(w: Int, h: Int): MutableList<MutableList<Boolean>> {
        return (0 until h).map {
            y -> MutableList(w) { x -> y == 0 && x == 0 }
        }.toMutableList()
    }

    fun solve(weights_map: List<List<Int>>): Int {
        val (w, h)      = Pair(weights_map[0].size, weights_map.size)
        val visited_map = getVisitedMap(w, h)
        val stack       = mutableListOf(Pair(0, Pos(0, 0)))
        val dirs        = listOf(Pos(1, 0), Pos(-1, 0), Pos(0, 1), Pos(0, -1))

        while (stack.isNotEmpty()) {
            val (cost, pos) = stack.removeFirst()
            val children = dirs.map { pos + it }.filter {
                it.x in (0 until w)
                    && it.y in (0 until h)
                    && !visited_map[it.y][it.x]
            }.map { Pair(cost + weights_map[it.y][it.x], it) }

            if (pos == Pos(w - 1, h - 1))
                return cost

            children.forEach { child ->
                val idx = stack.binarySearch(
                    child,
                    { (cost_a, pos_a), (cost_b, pos_b) ->
                        if (cost_a != cost_b)
                            return@binarySearch cost_a - cost_b
                        return@binarySearch pos_a.compareTo(pos_b)
                    }
                )

                if (idx >= 0)
                    return@forEach

                stack.add(-idx - 1, child)
                visited_map[child.second.y][child.second.x] = true
            }
        }
        return -1
    }

    fun part1(lines: List<String>): Int {
        return solve(getWeightsMap(lines))
    }

    fun part2(lines: List<String>): Int {
        return solve(inflateWeightsMap(getWeightsMap(lines)))
    }

    val testInput = readInput("Day15_test")
    expect(part1(testInput), 40)
    expect(part2(testInput), 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
