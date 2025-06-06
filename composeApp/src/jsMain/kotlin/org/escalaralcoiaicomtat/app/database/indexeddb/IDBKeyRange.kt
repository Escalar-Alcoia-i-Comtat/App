package org.escalaralcoiaicomtat.app.database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBKeyRange */
external class IDBKeyRange {

    fun includes(value: Any?): Boolean

    companion object {
        fun lowerBound(x: Any?, open: Boolean): IDBKeyRange
        fun upperBound(y: Any?, open: Boolean): IDBKeyRange
        fun bound(x: Any?, y: Any?, lowerOpen: Boolean, upperOpen: Boolean): IDBKeyRange
        fun only(z: Any?): IDBKeyRange
    }
}

fun IDBKeyRange.Companion.lowerBound(x: String?, open: Boolean): IDBKeyRange =
    lowerBound(x, open)

fun IDBKeyRange.Companion.lowerBound(x: Int, open: Boolean): IDBKeyRange =
    lowerBound(x, open)

fun IDBKeyRange.Companion.lowerBound(x: Double, open: Boolean): IDBKeyRange =
    lowerBound(x, open)

fun IDBKeyRange.Companion.upperBound(x: String?, open: Boolean): IDBKeyRange =
    upperBound(x, open)

fun IDBKeyRange.Companion.upperBound(x: Int, open: Boolean): IDBKeyRange =
    upperBound(x, open)

fun IDBKeyRange.Companion.upperBound(x: Double, open: Boolean): IDBKeyRange =
    upperBound(x, open)

fun IDBKeyRange.Companion.bound(
    x: String?,
    y: String?,
    lowerOpen: Boolean,
    upperOpen: Boolean,
): IDBKeyRange = bound(x, y, lowerOpen, upperOpen)

fun IDBKeyRange.Companion.bound(
    x: Int,
    y: Int,
    lowerOpen: Boolean,
    upperOpen: Boolean,
): IDBKeyRange = bound(x, y, lowerOpen, upperOpen)

fun IDBKeyRange.Companion.bound(
    x: Double,
    y: Double,
    lowerOpen: Boolean,
    upperOpen: Boolean,
): IDBKeyRange = bound(x, y, lowerOpen, upperOpen)

fun IDBKeyRange.Companion.only(z: String?): IDBKeyRange = only(z)
fun IDBKeyRange.Companion.only(z: Int): IDBKeyRange = only(z)
fun IDBKeyRange.Companion.only(z: Double): IDBKeyRange = only(z)
