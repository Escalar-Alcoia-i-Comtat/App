@file:Suppress("NOTHING_TO_INLINE")

package database.indexeddb

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array

sealed external interface IDBKey : JsAny

inline fun IDBKey(
    value: JsAny,
): IDBKey = value.unsafeCast()

inline fun IDBKey(
    value: JsNumber,
): IDBKey = value.unsafeCast()

inline fun IDBKey(
    value: Int,
): IDBKey = value.toJsNumber().unsafeCast()

inline fun IDBKey(
    value: Double,
): IDBKey = value.toJsNumber().unsafeCast()

inline fun IDBKey(
    value: JsString,
): IDBKey = value.unsafeCast()

inline fun IDBKey(
    value: String,
): IDBKey = value.toJsString().unsafeCast()

inline fun IDBKey(
    value: JsDate,
): IDBKey = value.unsafeCast()

inline fun IDBKey(
    value: Uint8Array,
): IDBKey = value.unsafeCast()

inline fun IDBKey(
    value: ArrayBuffer,
): IDBKey = value.unsafeCast()

inline fun IDBKey(
    value: ArrayBufferView,
): IDBKey = value.unsafeCast()

inline fun IDBKey(
    value: JsArray<IDBKey>,
): IDBKey = value.unsafeCast()

inline fun IDBKey(
    value: Array<IDBKey>,
): IDBKey = JsArray<IDBKey>().also { ja ->
        for (i in value.indices) {
            ja[i] = value[i]
        }
    }.unsafeCast()

inline fun IDBKey(
    value: IDBKeyRange,
): IDBKey = value.unsafeCast()

inline fun IDBKey(
    key: IDBKey,
    vararg moreKeys: IDBKey,
): IDBKey = JsArray<IDBKey>()
    .apply {
        set(0, key)
        for (i in moreKeys.indices) {
            set(i + 1, moreKeys[i])
        }
    }.unsafeCast()
