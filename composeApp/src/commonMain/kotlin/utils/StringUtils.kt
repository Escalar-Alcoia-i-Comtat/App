package utils

fun CharSequence.unaccent(): String {
    val original = "àáäèéëíìïòóöúùü"
    val normalized =  "aaaeeeiiiooouuu"

    return this.map {
        val index = original.indexOf(it.toString())
        if (index >= 0) normalized[index] else it
    }.joinToString("")
}
