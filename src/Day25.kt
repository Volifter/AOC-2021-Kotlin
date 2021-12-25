fun main() {
    class Cucumber(var x: Int, var y: Int, val dirX: Int, val dirY: Int) {
        var willMove = false

        fun getNewPos(map: List<List<Boolean>>): Pair<Int, Int> {
            val height = map.size
            val width = map.first().size

            return Pair((x + dirX) % width, (y + dirY) % height)
        }

        fun setWillMove(map: List<List<Boolean>>) {
            val (newX, newY) = getNewPos(map)

            willMove = !map[newY][newX]
        }

        fun move(map: List<MutableList<Boolean>>): Boolean {
            if (!willMove)
                return false

            val (newX, newY) = getNewPos(map)

            map[y][x] = false

            x = newX
            y = newY

            map[y][x] = true

            return true
        }
    }

    class Field(
        val map: List<MutableList<Boolean>>,
        val rightCucumbers: List<Cucumber>,
        val downCucumbers: List<Cucumber>
    ) {
        private fun moveCucumbers(cucumbers: List<Cucumber>): Boolean {
            cucumbers.forEach { it.setWillMove(map) }

            return cucumbers.fold(false) { moved, cucumber ->
                cucumber.move(map) || moved
            }
        }

        fun move(): Boolean {
            val movedRight = moveCucumbers(rightCucumbers)
            val movedDown = moveCucumbers(downCucumbers)

            return movedRight || movedDown
        }

        override fun toString(): String {
            val result = List(map.size) {
                MutableList(map.first().size) { '.' }
            }
            rightCucumbers.forEach {
                cucumber -> result[cucumber.y][cucumber.x] = '>'
            }
            downCucumbers.forEach {
                cucumber -> result[cucumber.y][cucumber.x] = 'v'
            }

            return result.joinToString ("\n") { it.joinToString("") } + "\n"
        }
    }

    fun parseField(lines: List<String>): Field {
        val rightCucumbers = mutableListOf<Cucumber>()
        val downCucumbers = mutableListOf<Cucumber>()
        val map = List(lines.size) {
            MutableList(lines.first().length) { false }
        }

        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                when (c) {
                    '>' -> {
                        map[y][x] = true
                        rightCucumbers.add(Cucumber(x, y, 1, 0))
                    }
                    'v' -> {
                        map[y][x] = true
                        downCucumbers.add(Cucumber(x, y, 0, 1))
                    }
                    '.' -> {}
                    else -> throw RuntimeException(
                        "Invalid character in map: $c"
                    )
                }
            }
        }

        return Field(map, rightCucumbers, downCucumbers)
    }

    fun part1(lines: List<String>): Int {
        val field = parseField(lines)

        return generateSequence { field.move().takeIf { it } }.count() + 1
    }

    val testInput = readInput("Day25_test")
    expect(part1(testInput), 58)

    val input = readInput("Day25")
    println(part1(input))
}
