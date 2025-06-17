package org.escalaralcoiaicomtat.app.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

actual val Dispatchers.IO: CoroutineDispatcher
    get() = Default

@OptIn(DelicateCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
fun <T> runBlocking(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): T = GlobalScope.promise(context) { block() } as T
