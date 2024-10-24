package cache

class ZipFile {
    private val files: MutableMap<String, ByteArray> = mutableMapOf()

    fun write(name: String, data: ByteArray) {
        files[name] = data
    }

    fun read(name: String): ByteArray? {
        return files[name]
    }
}
