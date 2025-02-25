package database.indexeddb

/** https://developer.mozilla.org/en-US/docs/Web/API/IDBKeyRange */
external class IDBKeyRange : JsAny {

    fun includes(value: JsAny?): Boolean

    companion object {
        fun lowerBound(x: JsAny?, open: Boolean): IDBKeyRange
        fun upperBound(y: JsAny?, open: Boolean): IDBKeyRange
        fun bound(x: JsAny?, y: JsAny?, lowerOpen: Boolean, upperOpen: Boolean): IDBKeyRange
        fun only(z: JsAny?): IDBKeyRange
    }
}

fun IDBKeyRange.Companion.lowerBound(x: String?, open: Boolean): IDBKeyRange =
    lowerBound(x?.toJsString(), open)

fun IDBKeyRange.Companion.lowerBound(x: Int, open: Boolean): IDBKeyRange =
    lowerBound(x.toJsNumber(), open)

fun IDBKeyRange.Companion.lowerBound(x: Double, open: Boolean): IDBKeyRange =
    lowerBound(x.toJsNumber(), open)

fun IDBKeyRange.Companion.upperBound(x: String?, open: Boolean): IDBKeyRange =
    upperBound(x?.toJsString(), open)

fun IDBKeyRange.Companion.upperBound(x: Int, open: Boolean): IDBKeyRange =
    upperBound(x.toJsNumber(), open)

fun IDBKeyRange.Companion.upperBound(x: Double, open: Boolean): IDBKeyRange =
    upperBound(x.toJsNumber(), open)

fun IDBKeyRange.Companion.bound(
    x: String?,
    y: String?,
    lowerOpen: Boolean,
    upperOpen: Boolean,
): IDBKeyRange = bound(x?.toJsString(), y?.toJsString(), lowerOpen, upperOpen)

fun IDBKeyRange.Companion.bound(
    x: Int,
    y: Int,
    lowerOpen: Boolean,
    upperOpen: Boolean,
): IDBKeyRange = bound(x.toJsNumber(), y.toJsNumber(), lowerOpen, upperOpen)

fun IDBKeyRange.Companion.bound(
    x: Double,
    y: Double,
    lowerOpen: Boolean,
    upperOpen: Boolean,
): IDBKeyRange = bound(x.toJsNumber(), y.toJsNumber(), lowerOpen, upperOpen)

fun IDBKeyRange.Companion.only(z: String?): IDBKeyRange = only(z?.toJsString())
fun IDBKeyRange.Companion.only(z: Int): IDBKeyRange = only(z.toJsNumber())
fun IDBKeyRange.Companion.only(z: Double): IDBKeyRange = only(z.toJsNumber())
