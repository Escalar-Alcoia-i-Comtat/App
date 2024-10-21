package utils

import io.github.aakira.napier.Napier
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipFile

/**
 * UnzipUtils class extracts files and sub-directories of a standard zip file to
 * a destination directory.
 */
object UnzipUtils {
    /**
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    @Throws(IOException::class)
    fun unzip(zipFilePath: File, destDirectory: File) {
        if (!destDirectory.exists()) {
            destDirectory.mkdirs()
        }

        ZipFile(zipFilePath).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->
                    val file = File(destDirectory, entry.name)

                    if (!entry.isDirectory) {
                        // if the entry is a file, extracts it
                        Napier.d { "Extracting file: ${file.relativeTo(destDirectory)}" }
                        extractFile(input, file)
                    } else {
                        // if the entry is a directory, make the directory
                        file.mkdir()
                        Napier.d { "Making dir: ${file.relativeTo(destDirectory)}" }
                    }
                }
            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     * @param inputStream
     * @param destFile
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun extractFile(inputStream: InputStream, destFile: File) {
        destFile.parentFile?.mkdirs()
        destFile.outputStream().buffered().use { bos ->
            val bytesIn = ByteArray(BUFFER_SIZE)
            var read: Int
            while (inputStream.read(bytesIn).also { read = it } != -1) {
                bos.write(bytesIn, 0, read)
            }
        }
    }

    /**
     * Size of the buffer to read/write data
     */
    private const val BUFFER_SIZE = 4096

}


/**
Copyright 2020 Nitin Prakash
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */