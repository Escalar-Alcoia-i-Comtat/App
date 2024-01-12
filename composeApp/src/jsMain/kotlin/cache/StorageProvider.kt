package cache

import okio.Path.Companion.toPath

actual class StorageProvider {
    actual val cacheDirectory: File
        get() = File(tmpdir().toPath().toString())
}

@JsModule("os")
@JsNonModule
external fun tmpdir(): String
