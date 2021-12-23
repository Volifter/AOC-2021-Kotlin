import java.util.PriorityQueue
import kotlin.math.pow

fun main() {
    data class Field(
        var corridor: MutableList<Char>,
        var rooms: MutableList<MutableList<Char>>,
        var cost: Int = 0
    ) {
        val isSolved: Boolean
            get() = this.rooms.withIndex().all {(i, room) ->
                room.all { it == 'A' + i }
            }

        fun cloneWithCost(cost: Int): Field = Field(
            this.corridor.toMutableList(),
            this.rooms.map { it.toMutableList() }.toMutableList(),
            this.cost + cost
        )

        fun getPathToRoom(start: Int, room_idx: Int): IntRange {
            val target = 2 * (room_idx + 1)

            return if (target > start) start + 1..target else target until start
        }

        fun getChildrenFromCorridor(): List<Field> {
            val children = mutableListOf<Field>()

            this.corridor.mapIndexed { i, c ->
                if (c == '.')
                    return@mapIndexed

                val room_idx = c - 'A'
                val target_room = this.rooms[room_idx]

                if (target_room.any { it != '.' && it != c })
                    return@mapIndexed

                val price = 10.0.pow(room_idx).toInt()
                val range = this.getPathToRoom(i, room_idx)

                if (range.any { this.corridor[it] != '.' })
                    return@mapIndexed

                val child = this.cloneWithCost(
                    price * (
                        range.last - range.first + 1
                            + target_room.count { it == '.' }
                    )
                ).also {
                    val room = it.rooms[room_idx]

                    room[room.lastIndexOf('.')] = c
                    it.corridor[i] = '.'
                }

                children.add(child)
            }

            return children
        }

        fun getChildrenFromRooms(): List<Field> {
            val children = mutableListOf<Field>()

            this.rooms.mapIndexed { room_idx, room ->
                val element_idx = room.indexOfFirst { it != '.' }

                if (element_idx == -1)
                    return@mapIndexed

                (0..10).forEach { target ->
                    if (target in 2..8 step 2 || this.corridor[target] != '.')
                        return@forEach

                    val range = getPathToRoom(target, room_idx)

                    if (range.any { this.corridor[it] != '.' })
                        return@forEach

                    val c = room[element_idx]
                    val price = 10.0.pow(c.code - 'A'.code).toInt()
                    val child = this.cloneWithCost(
                        price * (
                            range.last - range.first + 1
                                + 1
                                + element_idx
                            )
                    ).also {
                        it.corridor[target] = c
                        it.rooms[room_idx][element_idx] = '.'
                    }

                    children.add(child)
                }
            }

            return children
        }

        override fun toString(): String =
            listOf(
                "############# (cost ${this.cost})",
                "#" + this.corridor.joinToString("") + "#",
                (0 until this.rooms[0].size).joinToString("\n") { i ->
                    val sides = if (i == 0) "##" else "  "

                    sides +
                        "#" +
                        this.rooms.joinToString("#") { it[i].toString() } +
                        "#" +
                        sides.trim()
                },
                "  #########"
            ).joinToString("\n")

        override fun hashCode(): Int {
            return this.corridor.hashCode() * 31 + this.rooms.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return other is Field
                && other.corridor == this.corridor
                && other.rooms == this.rooms
        }
    }

    fun parseField(lines: List<String>): Field {
        val size = lines.size - 3
        val field = Field(
            MutableList(11) { '.' },
            MutableList(4) { MutableList(size) { '.' } }
        )

        lines.slice(2 until 2 + size).forEachIndexed { y, line ->
            (0..3).forEach { x ->
                field.rooms[x][y] = line[3 + x * 2]
            }
        }

        return field
    }

    fun solve(root: Field): Int? {
        val queue = PriorityQueue<Field> { a, b -> a.cost - b.cost }
        val processed = mutableMapOf<Field, Int>()

        fun addChildren(children: List<Field>) {
            children.forEach { child ->
                val cost = processed[child]

                if (cost != null && cost < child.cost)
                    return@forEach
                if (cost != null)
                    queue.remove(child)

                queue.add(child)
                processed[child] = child.cost
            }
        }

        queue.add(root)

        while (queue.isNotEmpty()) {
            val field = queue.remove()

            if (field.isSolved)
                return field.cost

            addChildren(field.getChildrenFromCorridor())
            addChildren(field.getChildrenFromRooms())
        }

        return null
    }

    fun part1(lines: List<String>): Int {
        val field = parseField(lines)

        return solve(field)!!
    }

    fun part2(lines: List<String>): Int {
        val field = parseField(
            lines.take(3)
                + listOf("  #D#C#B#A#", "  #D#B#A#C#")
                + lines.drop(3)
        )

        return solve(field)!!
    }

    val testInput = readInput("Day23_test")
    expect(part1(testInput), 12521)
    expect(part2(testInput), 44169)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}
