@file:Suppress("NOTHING_TO_INLINE")

package org.escalaralcoiaicomtat.app.database.indexeddb

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array
import kotlin.js.collections.JsArray

sealed external interface IDBKey

inline fun IDBKey(
    value: Any,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: Number,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: Int,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: Double,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: String,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: JsDate,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: Uint8Array,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: ArrayBuffer,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: ArrayBufferView,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: JsArray<IDBKey>,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: Array<IDBKey>,
): IDBKey = emptyArray<IDBKey>().also { ja ->
        for (i in value.indices) {
            ja[i] = value[i]
        }
    }.unsafeCast<IDBKey>()

inline fun IDBKey(
    value: IDBKeyRange,
): IDBKey = value.unsafeCast<IDBKey>()

inline fun IDBKey(
    key: IDBKey,
    vararg moreKeys: IDBKey,
): IDBKey = arrayOf<IDBKey>()
    .apply {
        set(0, key)
        for (i in moreKeys.indices) {
            set(i + 1, moreKeys[i])
        }
    }.unsafeCast<IDBKey>()
