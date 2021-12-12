class Node(private val name: String) {
    private val isBig     = name[0].isUpperCase()
    private var neighbors = emptyList<Node>().toMutableList()

    fun addNeighbor(node: Node) { this.neighbors += node }

    fun findPath(
        duplicationsLeft: Int = 0,
        visited: Set<Node> = emptySet()
    ): Int {
        return if (this.name == "end") 1 else this.neighbors.sumOf { node ->
            if (node.isBig || node !in visited)
                return@sumOf node.findPath(duplicationsLeft, visited + this)

            if (duplicationsLeft > 0 && node.name != "start")
                return@sumOf node.findPath(duplicationsLeft - 1, visited + this)

            return@sumOf 0
        }
    }

    override fun toString(): String {
        return "Node(" + this.name + ")"
    }
}

fun main() {
    fun buildGraph(lines: List<String>): Node {
        val nodes = emptyMap<String, Node>().toMutableMap()

        lines.forEach { line ->
            val (a, b) = line.split("-").map { name ->
                if (!nodes.contains(name))
                    nodes[name] = Node(name)

                nodes[name]!!
            }

            a.addNeighbor(b)
            b.addNeighbor(a)
        }

        return nodes["start"]!!
    }

    fun part1(lines: List<String>): Int {
        return buildGraph(lines).findPath()
    }

    fun part2(lines: List<String>): Int {
        return buildGraph(lines).findPath(1)
    }

    val testInput = readInput("Day12_test")
    expect(part1(testInput), 226)
    expect(part2(testInput), 3509)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
