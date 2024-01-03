package cache

import okio.FileSystem

actual val fileSystem: FileSystem get() = FileSystem.SYSTEM
