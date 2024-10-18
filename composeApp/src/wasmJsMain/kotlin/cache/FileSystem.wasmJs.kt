package cache

import kotlinx.io.files.FileSystem
import kotlinx.io.files.SystemFileSystem

actual val fileSystem: FileSystem get() = SystemFileSystem
