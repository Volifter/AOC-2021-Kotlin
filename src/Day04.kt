class Board(lines: List<String>) {
    private val rows = lines.map { line ->
        line.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }
    }
    private val columns = IntRange(0, 4).toList().map { y ->
        IntRange(0, 4).toList().map { x -> this.rows[x][y] }
    }
    private var lines = (rows + columns).map { Pair(it, 0) }
    var isDone = false
    var sum = this.rows.flatten().sum()

    fun addNumber(n: Int) {
        var found = false

        this.lines = this.lines.map { line ->
            if (line.first.contains(n)) {
                found = true
                if (line.second == 4)
                    this.isDone = true
                return@map Pair(line.first, line.second + 1)
            }
            line
        }
        if (found)
            this.sum -= n
    }

    override fun toString(): String {
        return "Board(" + this.lines + ")"
    }
}

fun main() {
    fun part1(lines: List<String>): Int {
        val data    = lines.filter { it.isNotEmpty() }
        val numbers = data[0].split(",").map { it.toInt() }
        val boards  = data.drop(1).chunked(5).map { Board(it) }

        numbers.forEach { n ->
            boards.forEach { board ->
                board.addNumber(n)
                if (board.isDone)
                    return n * board.sum
            }
        }
        return -1
    }

    fun part2(lines: List<String>): Int {
        val data    = lines.filter { it.isNotEmpty() }
        val numbers = data[0].split(",").map { it.toInt() }
        val boards  = data.drop(1).chunked(5).map { Board(it) }

        numbers.forEach { n ->
            boards.forEach { board ->
                board.addNumber(n)
                if (boards.all { it.isDone })
                    return n * board.sum
            }
        }
        return -1
    }

    val testInput = readInput("Day04_test")
    expect(part1(testInput), 4512)
    expect(part2(testInput), 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
