import kotlin.math.absoluteValue

fun main() {
    class RotCoord(val coord: Int, val sign: Int) {
        operator fun unaryMinus(): RotCoord {
            return RotCoord(this.coord, -this.sign)
        }

        override fun toString(): String =
            (if (this.sign == -1) "-" else "") + "xyz"[this.coord]
    }

    class Rotation(
        val x: RotCoord = RotCoord(0, 1),
        val y: RotCoord = RotCoord(1, 1),
        val z: RotCoord = RotCoord(2, 1)
    ) {
        operator fun get(i: Int): RotCoord = when (i) {
            0 -> this.x
            1 -> this.y
            2 -> this.z
            else -> throw RuntimeException("invalid coordinate access")
        }

        operator fun times(tr: Rotation): Rotation {
            return Rotation(
                RotCoord(
                    this[tr.x.coord].coord,
                    this[tr.x.coord].sign * tr.x.sign
                ),
                RotCoord(
                    this[tr.y.coord].coord,
                    this[tr.y.coord].sign * tr.y.sign
                ),
                RotCoord(
                    this[tr.z.coord].coord,
                    this[tr.z.coord].sign * tr.z.sign
                )
            )
        }

        override fun toString(): String =
            "Rotation(${this.x}, ${this.y}, ${this.z})"
    }

    val TRANSFORMS = run {
        var rot        = Rotation()
        val transforms = mutableListOf<Rotation>()

        repeat(6) { i ->
            repeat(3) {
                transforms.add(rot)
                rot = if (i and 1 == 0)
                    Rotation(rot.x, -rot.z, rot.y)
                else
                    Rotation(rot.x, rot.z, -rot.y)
            }
            transforms.add(rot)
            rot = Rotation(-rot.z, rot.y, rot.x)
        }
        return@run transforms
    }

    class Coords(val x: Int, val y: Int, val z: Int) {
        val delta: Int
            get() = (0..2).sumOf { this[it].absoluteValue }

        operator fun get(i: Int): Int = when (i) {
            0 -> this.x
            1 -> this.y
            2 -> this.z
            else -> throw RuntimeException("invalid coordinate access")
        }

        operator fun times(tr: Rotation): Coords {
            return Coords(
                this[tr.x.coord] * tr.x.sign,
                this[tr.y.coord] * tr.y.sign,
                this[tr.z.coord] * tr.z.sign
            )
        }

        operator fun plus(other: Coords): Coords {
            return Coords(this.x + other.x, this.y + other.y, this.z + other.z)
        }

        operator fun minus(other: Coords): Coords {
            return Coords(this.x - other.x, this.y - other.y, this.z - other.z)
        }

        override operator fun equals(other: Any?): Boolean {
            return other is Coords
                && this.x == other.x
                && this.y == other.y
                && this.z == other.z
        }

        override fun hashCode() = this.x + 997 * (this.y + 997 * this.z)

        override fun toString(): String =
            "Coords(${this.x}, ${this.y}, ${this.z})"
    }

    class Scanner(val id: Int, val coords: List<Coords>) {
        var visited: Boolean    = false
        var offset: Coords?     = Coords(0, 0, 0).takeIf { this.id == 0 }
        var rotation: Rotation? = TRANSFORMS[0].takeIf { this.id == 0 }

        val relativeCoords: List<Coords>
            get() = this.coords.map { it * this.rotation!! + this.offset!! }

        fun findOverlaps(other: Scanner): Boolean {
            TRANSFORMS.forEach { transform ->
                val rotations = other.coords.map { it * transform }.toSet()

                rotations.forEach { other_coords ->
                    this.coords.forEach { self_coords ->
                        val offset = other_coords - self_coords

                        if (
                            this.coords.count { it + offset in rotations } > 11
                        ) {
                            other.offset = this.offset!! -
                                offset * this.rotation!!
                            other.rotation = transform * this.rotation!!

                            return true
                        }
                    }
                }
            }
            return false
        }

        fun findOverlaps(beacons: Set<Scanner>) {
            if (this.visited)
                return

            beacons.forEach { other ->
                if (this.findOverlaps(other))
                    other.findOverlaps(beacons - other)
            }

            this.visited = true
        }

        fun distanceTo(other: Scanner): Int = (
            this.offset!! - other.offset!!
        ).delta

        override fun toString() = (
            "=== " +
                "Beacon #${this.id} " +
                "(offset ${this.offset}, " +
                "rotation ${this.rotation}) " +
            "===\n"
            + this.coords.joinToString("\n") { "  $it" }
        )
    }

    fun parseScanners(lines: List<String>): List<Scanner> {
        return lines.fold(
            emptyList<MutableList<Coords>>()
        ) { acc, line ->
            if (line.isEmpty())
                return@fold acc

            if (line.startsWith("---"))
                return@fold acc + listOf(mutableListOf())

            return@fold acc.also {
                it.last() += line
                    .split(",")
                    .map { n -> n.toInt() }
                    .let { (x, y, z) -> Coords(x, y, z) }
            }
        }.mapIndexed { i, coords -> Scanner(i, coords) }
    }

    fun part1(lines: List<String>): Int {
        val scanners = parseScanners(lines)

        scanners[0].findOverlaps(scanners.drop(1).toSet())

        return scanners.flatMap { scanner ->
            scanner.relativeCoords
        }.distinct().size
    }

    fun part2(lines: List<String>): Int {
        val scanners = parseScanners(lines)

        scanners[0].findOverlaps(scanners.drop(1).toSet())

        return scanners.withIndex().maxOf { (i, scanner) ->
            scanners.drop(i + 1).maxOfOrNull { other ->
                scanner.distanceTo(other)
            } ?: 0
        }
    }

    val testInput = readInput("Day19_test")
    expect(part1(testInput), 79)
    expect(part2(testInput), 3621)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}
