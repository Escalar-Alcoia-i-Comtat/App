package cache

import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

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

    private val _path: Path = path.toPath()

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

    val isDirectory: Boolean get() = fileSystem.metadataOrNull(_path)?.isDirectory ?: false

    val parent: File get() = File(path.substringBeforeLast('/'))

    fun write(bytes: ByteArray) {
        fileSystem.sink(_path).use { sink ->
            sink.buffer().use { bufferedSink ->
                bufferedSink.write(bytes)
            }
        }
    }

    fun readAllBytes(): ByteArray {
        return fileSystem.source(_path).use { source -> source.buffer().readByteArray() }
    }

    fun exists(): Boolean = fileSystem.exists(_path)

    fun delete() {
        if (isDirectory) fileSystem.deleteRecursively(_path)
        else fileSystem.delete(_path)
    }

    fun mkdirs() {
        fileSystem.createDirectories(_path)
    }

    /**
     * Lists all the files, returns null if doesn't exist or it's not a directory.
     */
    fun listAllFiles(): List<File>? {
        if (!exists()) return null
        if (!isDirectory) return null

        val files: List<Path>? = fileSystem.listOrNull(_path)
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
            size += fileSystem.metadata(_path).size ?: 0L
        }
        return size
    }
}
