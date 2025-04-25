package org.escalaralcoiaicomtat.app.platform.os

object OsCheck {
    private var detectedOS: OSType? = null

    fun getOSType(): OSType {
        if (detectedOS == null) {
            val os = System.getProperty("os.name", "generic").lowercase()
            detectedOS = when {
                os.contains("mac") || os.contains("darwin") -> OSType.MacOS
                os.contains("win") -> OSType.Windows
                os.contains("nux") -> OSType.Linux
                else -> OSType.Other
            }
        }
        return detectedOS!!
    }
}
