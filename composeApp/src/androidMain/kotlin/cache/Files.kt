package cache

actual object Files {
    private val File.file: java.io.File get() = java.io.File(path)

    val java.io.File.file: File get() = File(path)

    actual fun File.write(bytes: ByteArray) {
        file.writeBytes(bytes)
    }

    actual fun File.readAllBytes(): ByteArray {
        return file.readBytes()
    }

    actual fun File.exists(): Boolean = file.exists()

    actual fun File.delete(): Boolean = file.delete()

    actual fun File.mkdirs(): Boolean = file.mkdirs()
}
