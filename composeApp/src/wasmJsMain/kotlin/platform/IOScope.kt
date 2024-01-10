package platform

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// https://github.com/Kotlin/kotlinx.coroutines/issues/3205
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
