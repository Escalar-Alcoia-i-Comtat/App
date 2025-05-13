package org.escalaralcoiaicomtat.app.cache

class ZipFile {
    private val files: MutableMap<String, ByteArray> = mutableMapOf()

    fun write(name: String, data: ByteArray) {
        files[name] = data
    }

    fun append(name: String, data: ByteArray) {
        files[name] = (files[name] ?: byteArrayOf()) + data
    }

    fun read(name: String): ByteArray? {
        return files[name]
    }
}
