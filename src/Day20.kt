fun main() {
    class Image(filter: String, image: List<String>) {
        val width: Int
            get() = this.image[0].size
        val height: Int
            get() = this.image.size

        val filter = filter.map { if (it == '#') 1 else 0 }
        var image  = image.map { line -> line.map { if (it == '#') 1 else 0 } }

        operator fun contains(coords: Pair<Int, Int>): Boolean {
            val (x, y) = coords

            return x in (0 until this.width) && y in (0 until this.height)
        }

        operator fun get(x: Int, y: Int): Int? =
            if (Pair(x, y) in this) this.image[y][x] else null

        fun enhance(turn: Int) {
            this.image = (-1..this.height).map { y ->
                (-1..this.width).map { x ->
                    this.filter[(y - 1..y + 1).flatMap { _y ->
                        (x - 1..x + 1).map { _x ->
                            this[_x, _y] ?: (this.filter[0] and turn)
                        }
                    }.fold(0) { acc, n -> acc shl 1 or n }]
                }
            }
        }

        override fun toString(): String = "=== Image ===\n" +
            this.image.joinToString("\n") {
                it.joinToString("") { c -> ".#"[c].toString() }
            }

        fun countLit(): Int = this.image.sumOf { row -> row.sum() }
    }

    fun part1(lines: List<String>, n: Int): Int {
        val image = Image(lines[0], lines.drop(2))

        repeat(n) { i -> image.enhance(i and 1) }

        return image.countLit()
    }

    val testInput = readInput("Day20_test")
    expect(part1(testInput, 2), 35)
    expect(part1(testInput, 50), 3351)

    val input = readInput("Day20")
    println(part1(input, 2))
    println(part1(input, 50))
}
