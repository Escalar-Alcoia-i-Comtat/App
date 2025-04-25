package org.escalaralcoiaicomtat.app.platform.os

enum class OSType(
    val installerFormats: List<String> = emptyList(),
    val executableFormats: List<String> = emptyList()
) {
    Windows(
        installerFormats = listOf("msi"),
        executableFormats = listOf("exe")
    ),
    MacOS(
        installerFormats = listOf("dmg"),
        executableFormats = listOf("app")
    ),
    Linux(
        installerFormats = listOf("deb")
    ),
    Other
}
