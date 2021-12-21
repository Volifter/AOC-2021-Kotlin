fun main() {
    class Player(val id: Int, var pos: Int, var score: Int = 0) {
        fun move(n: Int) {
            this.pos = (this.pos + n) % 10
            this.score += this.pos + 1
        }

        fun copy(): Player = Player(this.id, this.pos, this.score)

        override fun equals(other: Any?): Boolean {
            return other is Player
                && this.id == other.id
                && this.pos == other.pos
                && this.score == other.score
        }

        override fun toString(): String = "Player(#${this.id} @ ${this.pos}: " +
            "${this.score})"

        override fun hashCode(): Int =
            this.id + (31 * (this.pos + 31 * this.score))
    }

    class GameState(
        val player_a: Player,
        val player_b: Player,
        val turn: Boolean
    ) {
        val currentPlayer
            get() = if (this.turn) this.player_b else this.player_a

        val playerPlayed
            get() = if (this.turn) this.player_a else this.player_b

        val isTerminal = this.playerPlayed.score >= 21

        fun play(n: Int): GameState {
            val player_a = this.player_a.let {
                if (!this.turn) it.copy().also { player -> player.move(n) }
                else it
            }
            val player_b = this.player_b.let {
                if (this.turn) it.copy().also { player -> player.move(n) }
                else it
            }
            return GameState(player_a, player_b, !this.turn)
        }

        override fun hashCode(): Int = Pair(player_a, player_b).hashCode() * 2 +
            if (this.turn) 1 else 0

        override fun equals(other: Any?): Boolean =
            other is GameState
                && this.player_a == other.player_a
                && this.player_b == other.player_b
                && this.turn == other.turn

        override fun toString(): String = "=== STATE ===\n" +
            "${this.player_a}\n${this.player_b}\n${this.turn}\n"
    }

    fun part1(lines: List<String>): Int {
        val (player_a, player_b) = lines.mapIndexed { i, line ->
            Player(i, line.substring(28).toInt() - 1)
        }
        var state = GameState(player_a, player_b, false)
        val dice  = generateSequence(1) { it % 100 + 1 }.iterator()
        var rolls = 0

        while (state.playerPlayed.score < 1e3) {
            state = state.play((0..2).sumOf { dice.next() })
            rolls++
        }

        return state.currentPlayer.score * rolls * 3
    }

    val FREQUENCIES = run {
        val THROWS_COUNT = 3
        val DICE_RANGE   = (1..3)

        return@run (0 until THROWS_COUNT - 1)
            .fold(DICE_RANGE.toList()) { acc, _ ->
                acc.flatMap { n -> DICE_RANGE.map { n + it } }
            }.groupingBy { it }.eachCount()
    }

    fun countWins(
        state: GameState,
        cache: MutableMap<GameState, Pair<Long, Long>> = mutableMapOf()
    ): Pair<Long, Long> {
        if (state.isTerminal)
            return if (state.turn) Pair(1, 0) else Pair(0, 1)

        var a_wins = 0L
        var b_wins = 0L

        FREQUENCIES.forEach { (n, freq) ->
            val new_state = state.play(n)

            if (new_state !in cache)
                cache[new_state] = countWins(new_state, cache)

            val (a, b) = cache[new_state]!!

            a_wins += a * freq
            b_wins += b * freq
        }
        return Pair(a_wins, b_wins)
    }

    fun part2(lines: List<String>): Long {
        val (player_a, player_b) = lines.mapIndexed { i, line ->
            Player(i, line.substring(28).toInt() - 1)
        }

        return countWins(GameState(player_a, player_b, false))
            .toList()
            .maxOf { it }
    }

    val testInput = readInput("Day21_test")
    expect(part1(testInput), 739785)
    expect(part2(testInput), 444356092776315)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
