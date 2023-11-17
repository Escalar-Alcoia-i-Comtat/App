package cache

class File(
    val path: String
) {
    /**
     * Obtains the last part of [path].
     */
    val name = path.split(Files.separator).last()

    constructor(file: File, path: String): this(
        path.substringBeforeLast(Files.separator) +
            Files.separator +
            file.path.substringAfter(Files.separator)
    )

    operator fun plus(other: File): File {
        return join(other)
    }

    operator fun plus(other: String): File {
        return join(other)
    }

    fun join(file: File): File {
        val before = path.substringBeforeLast(Files.separator)
        val after = file.path.substringAfter(Files.separator)

        return File("$before/$after")
    }

    fun join(path: String): File {
        val before = this.path.substringBeforeLast(Files.separator)
        val after = path.substringAfter(Files.separator)

        return File("$before/$after")
    }

    override fun toString(): String = path
}
