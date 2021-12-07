fun main() {
    fun part1(lines: List<String>): Int {
        val values = lines.map { it.toInt(2) }
        val length = lines[0].length
        var n      = 0

        for (idx in length - 1 downTo 0) {
            n = n shl 1
            if (values.count { (it shr idx) and 1 == 1 } > lines.size / 2)
                n = n or 1
        }
        return n * (n xor ((1 shl length) - 1))
    }

    fun part2(lines: List<String>): Int {
        val getMFB = { values: List<Int>, idx: Int -> if (
            values.count { (it shr idx) and 1 == 1 } >= values.size / 2.0
        ) 1 else 0 }

        val values = lines.map { it.toInt(2) }
        val length = lines[0].length
        var oxygen = values.toList()
        var co2    = values.toList()

        for (idx in length - 1 downTo 0) {
            if (oxygen.size > 1) {
                val mfb = getMFB(oxygen, idx)
                oxygen = oxygen.filter { (it shr idx) and 1 == mfb }
            }
            if (co2.size > 1) {
                val mfb = getMFB(co2, idx)
                co2 = co2.filter { (it shr idx) and 1 != mfb }
            }
        }
        return oxygen[0] * co2[0]
    }

    val testInput = readInput("Day03_test")
    expect(part1(testInput), 198)
    expect(part2(testInput), 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
