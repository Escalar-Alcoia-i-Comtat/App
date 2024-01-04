package map.utils

import map.Style

fun <S: Style> Iterable<S>.findById(id: String): S? = find { it.id == id }
