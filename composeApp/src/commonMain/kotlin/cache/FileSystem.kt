package cache

import kotlinx.io.files.FileSystem

@Deprecated(
    "Use KotlinX",
    replaceWith = ReplaceWith("SystemFileSystem", "kotlinx.io.files.SystemFileSystem")
)
expect val fileSystem: FileSystem
