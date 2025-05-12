package org.escalaralcoiaicomtat.app.utils

import org.escalaralcoiaicomtat.app.cache.File

/**
 * Converts a [File] to a [java.io.File].
 */
val File.asJavaFile: java.io.File
    get() = java.io.File(path)
