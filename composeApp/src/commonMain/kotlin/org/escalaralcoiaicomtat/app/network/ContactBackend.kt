package org.escalaralcoiaicomtat.app.network

import io.github.vinceglb.filekit.core.PlatformFile
import org.escalaralcoiaicomtat.app.utils.append

object ContactBackend : Backend() {
    suspend fun sendReport(
        name: String,
        email: String,
        message: String,
        files: List<PlatformFile>,
    ) {
        require(message.isNotEmpty()) { "Message cannot be empty." }

        val filesBytes = files.map { file ->
            Triple(file.name, file, file.readBytes())
        }

        submitForm("report") {
            name.takeUnless(String::isEmpty)?.let { append("name", it) }
            email.takeUnless(String::isEmpty)?.let { append("email", it) }
            append("message", message)

            for ((name, file, bytes) in filesBytes) {
                append(name, file, bytes)
            }
        }
    }
}
