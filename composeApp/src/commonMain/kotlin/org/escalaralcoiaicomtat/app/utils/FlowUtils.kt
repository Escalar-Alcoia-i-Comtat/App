package org.escalaralcoiaicomtat.app.utils

fun <S, A> S.applyIfNotNull(value: A?, block: S.(A) -> Unit): S {
    return if (value != null) {
        apply { block(value) }
    } else {
        this
    }
}
