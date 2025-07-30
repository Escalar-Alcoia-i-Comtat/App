package org.escalaralcoiaicomtat.app.network

import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import org.escalaralcoiaicomtat.app.utils.append

object ContactBackend : Backend() {
    suspend fun sendReport(
        name: String,
        email: String,
        message: String,
        sectorId: Long?,
        pathId: Long?,
        files: List<PlatformFile>,
    ) {
        require(message.isNotEmpty()) { "Message cannot be empty." }

        val filesBytes = files.map { file ->
            Triple(file.name, file, file.readBytes())
        }

        submitForm("report") {
            name.takeUnless(String::isEmpty)?.let { append("name", it) }
            email.takeUnless(String::isEmpty)?.let { append("email", it) }
            sectorId?.let { append("sectorId", it) }
            pathId?.let { append("pathId", it) }
            append("message", message)

            for ((name, file, bytes) in filesBytes) {
                append(name, file, bytes)
            }
        }
    }
}
