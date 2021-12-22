fun main() {
    class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange) {
        val volume: Long
            get() = 1L * this.x.count() * this.y.count() * this.z.count()

        fun isEmpty() = this.volume == 0L

        fun union(other: Cuboid): Cuboid = Cuboid(
            (
                maxOf(this.x.first, other.x.first)
                ..minOf(this.x.last, other.x.last)
            ), (
                maxOf(this.y.first, other.y.first)
                ..minOf(this.y.last, other.y.last)
            ), (
                maxOf(this.z.first, other.z.first)
                ..minOf(this.z.last, other.z.last)
            )
        )

        override fun toString(): String =
            "Cuboid(${this.x}, ${this.y}, ${this.z})"
    }

    fun parseCuboids(lines: List<String>): List<Pair<Cuboid, Boolean>> =
        lines.map { line ->
            val (is_on, coords) = line.split(" ")
            val (x, y, z)       = coords.split(",").map ranges@{
                val (from, to) = it
                    .substring(2)
                    .split("..")
                    .map {n -> n.toInt() }

                return@ranges (from..to)
            }

            return@map Pair(Cuboid(x, y, z), is_on == "on")
        }

    fun countLitCuboids(cuboids: List<Pair<Cuboid, Boolean>>): Long {
        val result = mutableListOf<Pair<Cuboid, Boolean>>()

        cuboids.forEach { (cuboid, is_on) ->
            result += result.map { (other, is_other_on) ->
                Pair(cuboid.union(other), !is_other_on)
            }.filterNot { it.first.isEmpty() }

            if (is_on)
                result += Pair(cuboid, true)
        }

        return result.sumOf { (cuboid, is_on) ->
            (if (is_on) 1 else -1) * cuboid.volume
        }
    }

    fun part1(lines: List<String>): Long =
        countLitCuboids(parseCuboids(lines).map { (cuboid, is_on) ->
            Pair(cuboid.union(Cuboid((-50..50), (-50..50), (-50..50))), is_on)
        })

    fun part2(lines: List<String>): Long =
        countLitCuboids(parseCuboids(lines))

    val testInput = readInput("Day22_test")
    expect(part1(testInput), 474140)
    expect(part2(testInput), 2758514936282235)

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}
