package map.utils

import map.style.Style

fun <S: Style> Iterable<S>.findById(id: String): S? = find { it.id == id }
