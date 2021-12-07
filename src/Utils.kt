import java.io.File

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("./inputs", "$name.txt").readLines()

fun <T> expect(got: T, expected: T) {
    if (got != expected)
        throw AssertionError("Assertion failed: expected $expected, got $got")
    println("Assertion passed: $got == $got")
}
