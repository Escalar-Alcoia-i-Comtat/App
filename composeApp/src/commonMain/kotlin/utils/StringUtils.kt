package utils

import kotlin.math.roundToInt

fun CharSequence.unaccent(): String {
    val original = "àáäèéëíìïòóöúùü"
    val normalized = "aaaeeeiiiooouuu"

    return this.map {
        val index = original.indexOf(it.toString())
        if (index >= 0) normalized[index] else it
    }.joinToString("")
}

private fun <Type: Number> formatDecimal(
    input: String,
    arg: Type,
    roundToInt: (Type) -> Int,
    round: (Type, positions: Int) -> Type
): String? {
    // Find the first occurrence of a float format
    val match = Regex("%(\\.\\d+)?f").find(input) ?: return null
    val index = match.range.first
    var decimalPositions = -1
    // Check whether the float format has decimal positions
    if (match.value[1] == '.') {
        // Get the position of the first non-numeric character after the decimal point
        // Example: "%.2f" -> 2
        val endNonNumeric = input.substring(index + 2).indexOfFirst { !it.isDigit() }
        decimalPositions = if (endNonNumeric == -1) {
            input.length - index - 2
        } else {
            input.substring(index + 2, index + 2 + endNonNumeric).toInt()
        }
    }
    return input.replaceRange(
        match.range,
        if (decimalPositions == 0)
            roundToInt(arg).toString()
        else
            round(arg, decimalPositions).toString()
    )
}

/**
 * Formats a string with the given arguments.
 *
 * The arguments can be strings, integers, floats, or doubles.
 *
 * The format specifiers are %s for strings, %d for integers, and %f for floats and doubles.
 *
 * For floats and doubles, you can specify the number of decimal positions with %.nf, where n is the
 * number of positions.
 *
 * If the number of decimal positions is not specified, it will be the default number of decimal positions.
 * @param args The arguments to replace the format specifiers.
 * @return The formatted string.
 */
fun String.format(vararg args: Any): String {
    var result = this
    @Suppress("LoopWithTooManyJumpStatements")
    for (arg in args) {
        if (arg is String) {
            result = result.replaceFirst("%s", arg)
        } else if (arg is Int) {
            result = result.replaceFirst("%d", arg.toString())
        } else if (arg is Long) {
            result = result.replaceFirst("%d", arg.toString())
        } else if (arg is Float) {
            result = formatDecimal(result, arg, Float::roundToInt, Float::round) ?: continue
        } else if (arg is Double) {
            result = formatDecimal(result, arg, Double::roundToInt, Double::round) ?: continue
        } else {
            result = result.replaceFirst("%s", arg.toString())
        }
    }
    return result
}
