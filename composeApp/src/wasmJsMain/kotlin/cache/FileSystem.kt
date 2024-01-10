package cache

import okio.FileSystem
import okio.NodeJsFileSystem

actual val fileSystem: FileSystem get() = NodeJsFileSystem
