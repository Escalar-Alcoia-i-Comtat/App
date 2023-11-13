package cache

expect object Files {
    fun File.write(bytes: ByteArray)

    fun File.readAllBytes(): ByteArray

    fun File.exists(): Boolean

    fun File.delete(): Boolean

    fun File.mkdirs(): Boolean
}
