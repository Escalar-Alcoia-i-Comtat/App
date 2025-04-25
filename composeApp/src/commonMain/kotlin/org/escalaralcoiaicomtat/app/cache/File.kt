package org.escalaralcoiaicomtat.app.cache

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

class File(
    val path: String
) {
    companion object {
        const val PATH_SEPARATOR: Char = '/'

        val Path.asFile: File get() = File(toString())
    }

    /**
     * Obtains the last part of [path].
     */
    val name = path.split(PATH_SEPARATOR).last()

    private val _path: Path = Path(path)

    constructor(file: File, path: String) : this(
        file.path.removeSuffix(PATH_SEPARATOR.toString()) +
            PATH_SEPARATOR +
            path.removePrefix(PATH_SEPARATOR.toString())
    )

    operator fun plus(other: File): File {
        return join(other)
    }

    operator fun plus(other: String): File {
        return join(other)
    }

    fun join(file: File): File {
        val before = path.removeSuffix(PATH_SEPARATOR.toString())
        val after = file.path.removePrefix(PATH_SEPARATOR.toString())

        return File("$before/$after")
    }

    fun join(path: String): File {
        val before = this.path.removeSuffix(PATH_SEPARATOR.toString())
        val after = path.removePrefix(PATH_SEPARATOR.toString())

        return File("$before/$after")
    }

    override fun toString(): String = path

    val isDirectory: Boolean get() = SystemFileSystem.metadataOrNull(_path)?.isDirectory ?: false

    val parent: File get() = File(path.substringBeforeLast('/'))

    /**
     * Writes the given [bytes] to the file. If [createParent] is true, it will create the parent
     * directories if they don't exist.
     *
     * @param bytes The bytes to write to the file.
     * @param createParent If true, it will create the parent directories if they don't exist.
     */
    fun write(bytes: ByteArray, createParent: Boolean = true) {
        if (createParent) parent.mkdirs()
        SystemFileSystem.sink(_path).use { sink ->
            sink.buffered().use { bufferedSink ->
                bufferedSink.write(bytes)
            }
        }
    }

    fun readAllBytes(): ByteArray {
        return SystemFileSystem.source(_path).use { source -> source.buffered().readByteArray() }
    }

    fun exists(): Boolean = SystemFileSystem.exists(_path)

    fun delete() {
        if (isDirectory) SystemFileSystem.deleteRecursively(_path)
        else SystemFileSystem.delete(_path)
    }

    fun mkdirs() {
        SystemFileSystem.createDirectories(_path)
    }

    /**
     * Lists all the files, returns null if doesn't exist or it's not a directory.
     */
    fun listAllFiles(): List<File>? {
        if (!exists()) return null
        if (!isDirectory) return null

        val files: List<Path>? = if (exists()) SystemFileSystem.list(_path).toList() else null
        return files?.map { it.asFile }
    }

    /**
     * The number of bytes readable from this file. The amount of storage resources consumed by this
     * file may be larger (due to block size overhead, redundant copies for RAID, etc.), or smaller
     * (due to file system compression, shared inodes, etc).
     *
     * @return This returns null if there is no file at path.
     */
    fun size(): Long? {
        if (!exists()) return null
        var size = 0L
        if (isDirectory) {
            listAllFiles()?.forEach {
                size += it.size() ?: 0L
            }
        } else {
            size += SystemFileSystem.metadataOrNull(_path)?.size ?: 0L
        }
        return size
    }
}
