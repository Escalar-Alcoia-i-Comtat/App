package org.escalaralcoiaicomtat.app.map.utils

import org.escalaralcoiaicomtat.app.map.style.Style

fun <S: Style> Iterable<S>.findById(id: String): S? = find { it.id == id }
