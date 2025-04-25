package org.escalaralcoiaicomtat.app.cache

import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path

fun FileSystem.deleteRecursively(path: Path, mustExist: Boolean = true) {
    if (metadataOrNull(path)?.isDirectory == true) {
        list(path).forEach {
            deleteRecursively(it, false)
        }
    }
    delete(path, mustExist)
}
