package cache

class File(
    val path: String
) {
    /**
     * Obtains the last part of [path].
     */
    val name = path.split(Files.separator).last()

    constructor(file: File, path: String): this(
        path.removeSuffix(Files.separator.toString()) +
            Files.separator +
            file.path.removePrefix(Files.separator.toString())
    )

    operator fun plus(other: File): File {
        return join(other)
    }

    operator fun plus(other: String): File {
        return join(other)
    }

    fun join(file: File): File {
        val before = path.removeSuffix(Files.separator.toString())
        val after = file.path.removePrefix(Files.separator.toString())

        return File("$before/$after")
    }

    fun join(path: String): File {
        val before = this.path.removeSuffix(Files.separator.toString())
        val after = path.removePrefix(Files.separator.toString())

        return File("$before/$after")
    }

    override fun toString(): String = path
}
