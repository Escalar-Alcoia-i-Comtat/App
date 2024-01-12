package platform

import build.BuildKonfig
import database.SettingsKeys
import database.settings
import github.data.Release
import io.github.aakira.napier.Napier
import io.github.z4kn4fein.semver.toVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.util.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import network.createHttpClient
import platform.os.OSType
import platform.os.OsCheck
import java.awt.Desktop
import java.io.File
import kotlin.system.exitProcess

actual object Updates {
    private val client = createHttpClient()

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }

    enum class Error {
        /** There are no assets in the release */
        NO_ASSETS,
        /** Release for current platform not found */
        RELEASE_NOT_FOUND,
        /** Internal exception, should not happen. The running OS was not recognized */
        UNKNOWN_OS,
        /** The release doesn't have the installer expected for the current OS */
        INSTALLER_NOT_FOUND
    }

    /** When the installer is being stored in the filesystem */
    const val DOWNLOAD_PROGRESS_STORING: Float = 2f
    /** When the installer has been started */
    const val DOWNLOAD_PROGRESS_INSTALLING: Float = 3f

    /**
     * Whether the platform supports updates.
     */
    actual val updatesSupported: Boolean = true

    /**
     * Whether there's an update available.
     */
    actual val updateAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * If supported, holds the name of the latest version available. Only applies if
     * [updateAvailable] is true.
     */
    actual val latestVersion: MutableStateFlow<String?> = MutableStateFlow(null)

    /**
     * If not null, specifies the download progress of the latest version.
     */
    val downloadProgress: MutableStateFlow<Float?> = MutableStateFlow(null)

    /**
     * Stores the error that happened during the update, if any.
     */
    val updateError: MutableStateFlow<Error?> = MutableStateFlow(null)

    private suspend fun getVersions(): List<Release>? {
        val result = client.get(
            "https://api.github.com/repos/Escalar-Alcoia-i-Comtat/App/releases"
        ) {
            header(HttpHeaders.Accept, "application/vnd.github+json")
            header("X-GitHub-Api-Version" , "2022-11-28")
            header(HttpHeaders.Authorization, BuildKonfig.GITHUB_TOKEN)
        }
        if (result.status.value !in 200..299) {
            Napier.w { "Could not check for updates. Error: ${result.status}" }
            return null
        }
        val rawResponse = result.bodyAsText()
        val jsonElement = json.decodeFromString<JsonElement>(rawResponse)
        val list = jsonElement.jsonArray
        return list
            .map { json.decodeFromJsonElement<Release>(it) }
            // Make sure version is SemVer
            .filter { release ->
                val version = release.tagName.toVersionOrNull()
                version != null
            }
            // Sort versions by SemVer
            .sortedWith { release1, release2 ->
                val version1 = release1.tagName.toVersion()
                val version2 = release2.tagName.toVersion()
                version1.compareTo(version2)
            }
    }

    /**
     * Asks GitHub for the latest release, and compares it with the currently running one.
     * If there's a new version, updates [updateAvailable] and [latestVersion], though if
     * [SettingsKeys.SKIP_VERSION] matches the new version, those are not updated.
     *
     * @return If there's a new version available, ignoring the value of [SettingsKeys.SKIP_VERSION]
     */
    suspend fun checkForUpdates(): Boolean? {
        Napier.d { "Checking for updates..." }
        val updates = getVersions() ?: return null
        val versions = updates.joinToString(", ") { it.tagName }
        Napier.d { "Available versions: $versions" }
        val latestVersion = updates.last().tagName.toVersion()
        val installedVersion = BuildKonfig.VERSION_NAME.toVersion()

        val skipVersion = settings.getStringOrNull(SettingsKeys.SKIP_VERSION) == latestVersion.toString()

        this.updateAvailable.emit(!skipVersion && latestVersion > installedVersion)
        this.latestVersion.emit(latestVersion.toString())

        if (latestVersion > installedVersion) {
            Napier.i { "There's a new version available: ${updates.first().tagName}" }
        } else {
            Napier.d { "Using the last version available ($latestVersion): $installedVersion" }
        }
        if (skipVersion) Napier.i { "Skip next version." }
        return latestVersion > installedVersion
    }

    private suspend fun runInstaller(osType: OSType, installer: File) {
        Napier.i { "Launching installer at: $installer" }
        if (osType == OSType.Windows) {
            val runtime = Runtime.getRuntime()
            val process = runtime.exec("msiexec /i \"$installer\"")
            downloadProgress.emit(DOWNLOAD_PROGRESS_INSTALLING)
            val input = process.inputStream.reader().buffered()
            var line: String? = input.readLine()
            while (line != null) {
                Napier.v(tag = "Installer") { line ?: "" }
                line = input.readLine()
            }
            val result = process.exitValue()
            if (result == 0) {
                val defaultLocation = File(System.getenv("LOCALAPPDATA"), "Escalar Alcoia i Comtat")
                val defaultInstaller = File(defaultLocation, "Escalar Alcoia i Comtat.exe")
                if (defaultInstaller.exists())
                    Desktop.getDesktop().open(defaultInstaller)
                exitProcess(0)
            }
            Napier.i { "Finished installer. Result: ${process.exitValue()}" }
        } else {
            Napier.i { "Installer is not MSI." }
            Desktop.getDesktop().open(installer)
            exitProcess(0)
        }
    }

    /**
     * Requests the device to update to the latest version available.
     *
     * @return The job that is performing the update, or null if updates are not available.
     */
    actual fun requestUpdate(): Job? = CoroutineScope(Dispatchers.IO).launch {
        downloadProgress.emit(null)

        val osType = OsCheck.getOSType()
        if (osType == OSType.Other) {
            updateError.emit(Error.UNKNOWN_OS)
            return@launch
        }

        val versions = getVersions() ?: return@launch
        val version = versions.last()
        if (version.assets.isEmpty()) {
            updateError.emit(Error.NO_ASSETS)
            return@launch
        }

        val assets = version.assets.joinToString(", ") { "${it.name}: ${it.url}" }
        Napier.i { "Assets: $assets" }
        val asset = version.assets.find { asset ->
            osType.installerFormats.find { asset.name.endsWith(it, ignoreCase = true) } != null
        }
        if (asset == null) {
            updateError.emit(Error.INSTALLER_NOT_FOUND)
            return@launch
        }

        val result = client.get(asset.browserDownloadUrl) {
            onDownload { bytesSentTotal, contentLength ->
                downloadProgress.emit(
                    (bytesSentTotal.toDouble() / contentLength.toDouble()).toFloat()
                )
            }
        }
        downloadProgress.emit(DOWNLOAD_PROGRESS_STORING)
        val installerFile = File.createTempFile("eaic", "installer")
        val bytes = result.bodyAsChannel().toByteArray()
        installerFile.outputStream().use { it.write(bytes) }

        runInstaller(osType, installerFile)

        downloadProgress.emit(null)
    }
}
