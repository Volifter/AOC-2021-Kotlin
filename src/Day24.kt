import kotlin.math.absoluteValue

enum class InstructionType { INP, ADD, MUL, DIV, MOD, EQL }

fun main() {
    val VARIABLE_VALUE_LIMIT = 5000000

    class ModelNumber(val numbers: List<Byte> = listOf()) {
        fun addInput(n: Byte) = ModelNumber(numbers + n)

        operator fun compareTo(other: ModelNumber): Int {
            numbers.zip(other.numbers).forEach { (self, others) ->
                if (self < others)
                    return -1

                if (self > others)
                    return 1
            }

            return 0
        }

        override fun toString(): String = numbers.joinToString("")
    }

    class State(
        val variables: List<Int>,
        var minNumber: ModelNumber = ModelNumber(),
        var maxNumber: ModelNumber = ModelNumber()
    ) {
        fun getChildState(
            variableIdx: Int,
            variableValue: Int,
            newMinNumber: ModelNumber? = null,
            newMaxNumber: ModelNumber? = null
        ): State {
            val vars = variables.toMutableList()

            vars[variableIdx] = variableValue

            return State(
                vars,
                newMinNumber ?: minNumber,
                newMaxNumber ?: maxNumber
            )
        }

        fun applyBest(other: State) {
            if (other.minNumber < minNumber)
                minNumber = other.minNumber
            if (other.maxNumber > maxNumber)
                maxNumber = other.maxNumber
        }

        operator fun get(i: Int) = variables[i]

        override fun toString(): String
            = "State(${variables}, $minNumber, $maxNumber)"
    }

    open class Value

    class ConstantValue(val value: Int) : Value() {
        override fun toString(): String = "ConstantValue($value)"
    }

    class VariableValue(val variableIdx: Int) : Value() {
        override fun toString(): String
            = "VariableInstruction(${"wxyz"[variableIdx]})"
    }

    open class Instruction(val type: InstructionType)

    class InputInstruction(
        val variable: VariableValue
    ) : Instruction(InstructionType.INP) {
        override fun toString(): String
            = "InputInstruction($variable)"
    }

    class OperationInstruction(
        type: InstructionType,
        val left: VariableValue,
        val right: Value
    ) : Instruction(type) {
        fun applyToState(state: State): State? {
            val lVal = state[left.variableIdx]
            val rVal = when (right) {
                is ConstantValue -> right.value
                is VariableValue -> state[right.variableIdx]
                else -> throw RuntimeException("Invalid value")
            }

            return when (type) {
                InstructionType.ADD -> {
                    state.getChildState(left.variableIdx, lVal + rVal)
                }
                InstructionType.MUL -> {
                    state.getChildState(left.variableIdx, lVal * rVal)
                }
                InstructionType.DIV -> {
                    if (rVal != 0)
                        state.getChildState(left.variableIdx, lVal / rVal)
                    else
                        null
                }
                InstructionType.MOD -> {
                    if (rVal > 0)
                        state.getChildState(left.variableIdx, lVal % rVal)
                    else
                        null
                }
                InstructionType.EQL -> {
                    state.getChildState(
                        left.variableIdx,
                        if (lVal == rVal) 1 else 0
                    )
                }
                else -> null
            }
        }

        override fun toString(): String
            = "OperationInstruction($type, $left, $right)"
    }

    fun parseInstructions(lines: List<String>): List<Instruction> {
        val variables = "wxyz"
            .withIndex()
            .associate { (i, c) -> c to VariableValue(i) }

        return lines.mapNotNull { line ->
            val parts = line.split(" ")
            val type = when (parts[0]) {
                "inp" -> InstructionType.INP
                "add" -> InstructionType.ADD
                "mul" -> InstructionType.MUL
                "div" -> InstructionType.DIV
                "mod" -> InstructionType.MOD
                "eql" -> InstructionType.EQL
                else -> {
                    println(
                        "Warning: skipping invalid instruction \"${parts[0]}\""
                    )
                    return@mapNotNull null
                }
            }

            val left = variables[parts[1][0]]
                ?: throw RuntimeException("Invalid variable name")

            if (type == InstructionType.INP)
                return@mapNotNull InputInstruction(left)

            val right: Value = parts[2]
                .toIntOrNull()?.let { ConstantValue(it) }
                ?: variables[parts[2][0]]
                ?: throw RuntimeException("Invalid variable name")

            return@mapNotNull OperationInstruction(type, left, right)
        }
    }

    fun mergeStates(states: Sequence<State>): List<State> {
        val mergedStates = mutableMapOf<List<Int>, State>()

        states.forEach { state ->
            if (state.variables !in mergedStates)
                mergedStates[state.variables] = state
            else
                mergedStates[state.variables]!!.applyBest(state)
        }

        return mergedStates.values.toList()
    }

    fun solve(lines: List<String>): Pair<String, String> {
        val instructions = parseInstructions(lines)
        var states = listOf(State(List(4) { 0 }))
        var i = 0

        instructions.forEach { instruction ->
            println("${++i} / ${instructions.size} (${states.size})")

            when (instruction) {
                is InputInstruction -> {
                    states = (sequence {
                        states.forEach { state ->
                            (1..9).forEach { n ->
                                yield(state.getChildState(
                                    instruction.variable.variableIdx,
                                    n,
                                    state.minNumber.addInput(n.toByte()),
                                    state.maxNumber.addInput(n.toByte())
                                ))
                            }
                        }
                    }).filter { state ->
                        state.variables.all {
                            it.absoluteValue < VARIABLE_VALUE_LIMIT
                        }
                    }.toList()
                }
                is OperationInstruction -> {
                    states = mergeStates(states.asSequence().mapNotNull { instruction.applyToState(it) })
                }
            }
        }
        val bestState = states
            .filter { it.variables.last() == 0 }
            .reduce { stateA, stateB -> stateA.applyBest(stateB); stateA }

        return Pair("" + bestState.minNumber, "" + bestState.maxNumber)
    }

    val testInput = readInput("Day24_test")

    expect(solve(testInput), Pair("113", "998"))

    val input = readInput("Day24")
    val (min, max) = solve(input)

    println("Min model number: $min")
    println("Max model number: $max")
}
