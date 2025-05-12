package org.escalaralcoiaicomtat.app.cache

import kotlinx.io.files.Path
import kotlinx.io.files.SystemTemporaryDirectory
import org.escalaralcoiaicomtat.app.cache.File.Companion.asFile

actual class StorageProvider() {
    actual val cacheDirectory: File = Path(SystemTemporaryDirectory, ".escalaralcoiaicomtat").asFile
}
