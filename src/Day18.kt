fun main() {
    class SFNumber(var left: Any, var right: Any) {
        var parent: SFNumber? = null

        val isLeaf: Boolean
            get() = this.left is Int && this.right is Int
        val leftmostLeaf: SFNumber
            get() = (this.left as? SFNumber)?.leftmostLeaf ?: this
        val rightmostLeaf: SFNumber
            get() = (this.right as? SFNumber)?.rightmostLeaf ?: this
        val magnitude: Int
            get() = (
                (
                    ((this.left as? SFNumber)?.magnitude
                        ?: (this.left as Int)) * 3
                ) + (
                    ((this.right as? SFNumber)?.magnitude
                        ?: (this.right as Int)) * 2
                )
            )

        fun setParents() {
            (this.left as? SFNumber)?.let {
                it.setParents()
                it.parent = this
            }
            (this.right as? SFNumber)?.let {
                it.parent = this
                it.setParents()
            }
        }

        fun addToLeft(n: Int) {
            if (this.left is SFNumber)
                return (this.left as SFNumber).rightmostLeaf.addToRight(n)
            this.left = (this.left as Int) + n
        }

        fun addToRight(n: Int) {
            if (this.right is SFNumber)
                return (this.right as SFNumber).leftmostLeaf.addToLeft(n)
            this.right = (this.right as Int) + n
        }

        fun walk(depth: Int = 0): Sequence<Pair<SFNumber, Int>> = sequence {
            if (this@SFNumber.left is Int)
                yield(Pair(this@SFNumber, depth))
            (this@SFNumber.left as? SFNumber)?.let {
                yieldAll(it.walk(depth + 1))
            }
            if (this@SFNumber.left !is Int && this@SFNumber.right is Int)
                yield(Pair(this@SFNumber, depth))
            (this@SFNumber.right as? SFNumber)?.let {
                yieldAll(it.walk(depth + 1))
            }
        }

        fun explode() {
            var current       = this
            var parent        = this.parent
            var (left, right) = Pair(this.left as Int?, this.right as Int?)

            while (parent !== null && (left !== null || right !== null)) {
                if (left !== null && parent.left !== current) {
                    parent.addToLeft(left)
                    left = null
                }
                if (right !== null && parent.right !== current) {
                    parent.addToRight(right)
                    right = null
                }
                current = parent
                parent = current.parent
            }
            if (this.parent?.left === this)
                this.parent!!.left = 0
            if (this.parent?.right === this)
                this.parent!!.right = 0
        }

        fun split(): Boolean {
            (this.left as? Int)?.let {
                if (it > 9) {
                    this.left = SFNumber(it / 2, it - it / 2)
                    (this.left as SFNumber).parent = this
                    return true
                }
            }
            (this.right as? Int)?.let {
                if (it > 9) {
                    this.right = SFNumber(it / 2, it - it / 2)
                    (this.right as SFNumber).parent = this
                    return true
                }
            }
            return false
        }

        fun reduce() {
            while (
                this.walk().any { (child, depth) ->
                    if (depth < 4 || !child.isLeaf)
                        return@any false

                    child.explode()

                    return@any true
                } || this.walk().any { (child, _) ->
                    child.split()
                }
            );
        }

        fun copy(): SFNumber {
            return SFNumber(
                (this.left as? SFNumber)?.copy() ?: this.left,
                (this.right as? SFNumber)?.copy() ?: this.right
            )
        }

        operator fun plus(other: SFNumber): SFNumber {
            return SFNumber(this.copy(), other.copy())
        }

        override fun toString(): String {
            return "[${this.left}, ${this.right}]"
        }
    }

    fun makeSFNumber(str: String): Any {
        if (!str.startsWith("["))
            return str.toInt()

        val content = str.substring(1, str.lastIndex)
        val i = content.let {
            var d = 0

            it.indexOfFirst { c ->
                if (c == '[')
                    d++
                if (c == ']')
                    d--
                return@indexOfFirst c == ',' && d == 0
            }
        }

        return SFNumber(
            makeSFNumber(content.substring(0, i)),
            makeSFNumber(content.substring(i + 1))
        )
    }

    fun part1(lines: List<String>): Int {
        val numbers = lines.map { str -> makeSFNumber(str) as SFNumber }
        return numbers.reduce { a, b ->
            return@reduce (a + b).also {
                it.setParents()
                it.reduce()
            }
        }.magnitude
    }

    fun part2(lines: List<String>): Int {
        val numbers = lines.map { str -> makeSFNumber(str) as SFNumber }

        return numbers.maxOf { number_a ->
            numbers.filter { it !== number_a }.maxOf inner@{ number_b ->
                return@inner (number_a + number_b).also {
                    it.setParents()
                    it.reduce()
                }.magnitude
            }
        }
    }

    val testInput = readInput("Day18_test")
    expect(part1(testInput), 4140)
    expect(part2(testInput), 3993)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
