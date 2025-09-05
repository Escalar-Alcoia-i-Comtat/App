package org.escalaralcoiaicomtat.app.platform

import io.github.aakira.napier.Napier
import io.ktor.utils.io.core.readFully
import kotlinx.coroutines.await
import kotlinx.io.Buffer
import org.escalaralcoiaicomtat.app.cache.ZipFile
import org.escalaralcoiaicomtat.app.fs.JSZip
import org.escalaralcoiaicomtat.app.fs.JSZipObject
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.toUByteArray
import org.khronos.webgl.toUint8Array

private fun keys(array: JsAny): JsArray<JsString> = js("Object.keys(array)")

actual object ZipFileHandler {
    @OptIn(ExperimentalUnsignedTypes::class)
    private fun Buffer.toUint8Array(): Uint8Array {
        val byteArray = ByteArray(size.toInt())
        readFully(byteArray)
        return byteArray.toUByteArray().toUint8Array()
    }

    /**
     * Extracts a zip file into memory.
     */
    @OptIn(ExperimentalUnsignedTypes::class)
    actual suspend fun unzip(zipData: Buffer): ZipFile {
        Napier.d { "Converting zip buffer into array..." }
        val uint8 = zipData.toUint8Array()
        Napier.d { "Loading zip file (${uint8.byteLength} bytes) into memory..." }
        val zip: JSZip = JSZip.loadAsync(uint8)
            .also { Napier.d { "Awaiting result..." } }
            .await()

        val zipFile = ZipFile()

        Napier.d { "Extracting files into memory..." }
        val fileNames = keys(zip.files).toArray()
        for (name in fileNames) {
            val file: JSZipObject? = zip.file(name)
            if (file != null) {
                val content: Uint8Array = file.async("uint8array").await()
                zipFile.write(name.toString(), content.toUByteArray().toByteArray())
            }
        }

        return zipFile
    }
}
