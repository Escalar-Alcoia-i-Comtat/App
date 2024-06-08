package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal

/**
 * Returns the current value of the [ProvidableCompositionLocal] or throws an
 * [IllegalStateException] if no value is provided.
 */
val <T: Any> ProvidableCompositionLocal<T?>.currentOrThrow: T
    @Composable
    get() = current ?: error("No value provided for $this")
