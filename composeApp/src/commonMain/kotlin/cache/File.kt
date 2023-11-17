package cache

class File(
    val path: String
) {
    /**
     * Obtains the last part of [path].
     */
    val name = path.split(Files.separator).last()

    constructor(file: File, path: String): this(
        "${path.substringBeforeLast('/')}/${file.path.substringAfter('/')}"
    )

    operator fun plus(other: File): File {
        return join(other)
    }

    operator fun plus(other: String): File {
        return join(other)
    }

    fun join(file: File): File {
        val before = path.substringBeforeLast('/')
        val after = file.path.substringAfter('/')

        return File("$before/$after")
    }

    fun join(path: String): File {
        val before = this.path.substringBeforeLast('/')
        val after = path.substringAfter('/')

        return File("$before/$after")
    }

    override fun toString(): String = path
}
