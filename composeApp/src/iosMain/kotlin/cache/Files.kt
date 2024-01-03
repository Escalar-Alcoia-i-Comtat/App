package cache

import cache.Files.file
import cache.Files.readAllBytes
import cache.Files.write
import kotlinx.io.files.Path
import kotlinx.io.files.SystemPathSeparator

actual object Files {
    actual val separator: Char = SystemPathSeparator

    actual val File.isDirectory: Boolean get() = TODO()

    private val File.ioPath: Path get() = Path(path)

    actual fun File.write(bytes: ByteArray) {
        ioPath.write(bytes)
    }

    actual fun File.readAllBytes(): ByteArray {
        return file.readBytes()
    }

    actual fun File.exists(): Boolean = file.exists()

    actual fun File.delete(): Boolean = file.delete()

    actual fun File.mkdirs(): Boolean = file.mkdirs()

    actual fun File.listAllFiles(): List<File>? = file.listFiles()?.map { it.file }
}
