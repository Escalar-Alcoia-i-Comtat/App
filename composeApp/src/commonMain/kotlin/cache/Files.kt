package cache

expect object Files {
    val separator: Char

    val File.isDirectory: Boolean

    fun File.write(bytes: ByteArray)

    fun File.readAllBytes(): ByteArray

    fun File.exists(): Boolean

    fun File.delete(): Boolean

    fun File.mkdirs(): Boolean

    /**
     * Lists all the files, returns null if doesn't exist or it's not a directory.
     */
    fun File.listAllFiles(): List<File>?
}
