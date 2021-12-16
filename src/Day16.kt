fun main() {
    class Buffer(line: String) {
        val data: MutableList<Boolean> = line.flatMap { c ->
            c
                .toString()
                .toInt(16)
                .toString(2)
                .padStart(4, '0')
                .map { it == '1' }
        }.toMutableList()

        fun readInt(size: Int): Int {
            return (0 until size).fold(0) { acc, _ ->
                acc shl 1 or if (data.removeFirst()) 1 else 0
            }
        }

        val size get(): Int = this.data.size

        override fun toString(): String {
            return this.data.joinToString("") { if (it) "1" else "0" }
        }
    }

    class Packet(val buffer: Buffer) {
        val version: Int            = buffer.readInt(3)
        val type: Int               = buffer.readInt(3)
        var value: Long?            = null
        var children: List<Packet>? = null

        init {
            when (this.type) {
                4    -> this.value = this.parseValue()
                else -> this.children = this.parseChildren()
            }
        }

        private fun parseValue(): Long {
            var n       = 0L
            var is_last = false

            while (!is_last) {
                is_last = this.buffer.readInt(1) == 0
                n       = (n shl 4) or this.buffer.readInt(4).toLong()
            }

            return n
        }

        private fun parseChildren(): List<Packet> {
            val type     = this.buffer.readInt(1)
            val size     = this.buffer.readInt(if (type == 0) 15 else 11)
            val children = emptyList<Packet>().toMutableList()

            if (type == 1)
                return (0 until size).map { Packet(this.buffer) }

            val target_size = this.buffer.size - size

            while (this.buffer.size > target_size)
                children += Packet(this.buffer)

            return children
        }

        fun sumVersions(): Int {
            return this.version + (
                this.children?.sumOf { it.sumVersions() } ?: 0
            )
        }

        fun getValue(): Long {
            return when (this.type) {
                0 -> this.children!!.sumOf { it.getValue() }
                1 -> this.children!!.fold(1) { acc, packet ->
                    acc * packet.getValue()
                }
                2 -> this.children!!.minOf { it.getValue() }
                3 -> this.children!!.maxOf { it.getValue() }
                4 -> this.value!!
                5 -> if (
                    this.children!![0].getValue()
                    > this.children!![1].getValue()
                ) 1 else 0
                6 -> if (
                    this.children!![0].getValue()
                    < this.children!![1].getValue()
                ) 1 else 0
                7 -> if (
                    this.children!![0].getValue()
                    == this.children!![1].getValue()
                ) 1 else 0
                else -> throw RuntimeException("invalid type")
            }
        }

        override fun toString(): String {
            if (this.type == 4)
                return "ValuePacket(#${this.version}, ${this.value})"

            return "OperatorPacket(#${this.version}, ${this.type}, " +
                "${this.children})"
        }
    }

    fun part1(line: String): Int {
        return Packet(Buffer(line)).sumVersions()
    }

    fun part2(line: String): Long {
        return Packet(Buffer(line)).getValue()
    }

    val testInput = readInput("Day16_test")
    expect(part1(testInput[0]), 16)
    expect(part1(testInput[1]), 12)
    expect(part1(testInput[2]), 23)
    expect(part1(testInput[3]), 31)

    expect(part2(testInput[5]), 3)
    expect(part2(testInput[6]), 54)
    expect(part2(testInput[7]), 7)
    expect(part2(testInput[8]), 9)
    expect(part2(testInput[9]), 1)
    expect(part2(testInput[10]), 0)
    expect(part2(testInput[11]), 0)
    expect(part2(testInput[12]), 1)

    val input = readInput("Day16")
    println(part1(input[0]))
    println(part2(input[0]))
}
