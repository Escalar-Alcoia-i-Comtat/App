package org.escalaralcoiaicomtat.app.cache

import android.content.Context

actual class StorageProvider(private val context: Context) {
    actual val cacheDirectory: File get() = File(context.cacheDir.path)
}
