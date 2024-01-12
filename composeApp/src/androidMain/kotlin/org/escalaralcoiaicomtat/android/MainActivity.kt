package org.escalaralcoiaicomtat.android

import App
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import data.EDataType
import io.github.aakira.napier.Napier
import platform.Updates

class MainActivity : ComponentActivity() {
    companion object {
        var instance: MainActivity? = null
            private set
    }

    private val updateResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            Napier.e { "Update failed. Code: ${result.resultCode}" }
            // TODO: Notify the user
        }
    }

    private val updateListener = InstallStateUpdatedListener { state ->
        val status = state.installStatus()
        if (status == InstallStatus.DOWNLOADING) {
            val bytesDownloaded = state.bytesDownloaded()
            val totalBytesToDownload = state.totalBytesToDownload()
            val progress = bytesDownloaded.toFloat() / totalBytesToDownload.toFloat()
            Toast.makeText(
                this,
                getString(R.string.downloading_update_progress, progress),
                Toast.LENGTH_LONG
            ).show()
        } else if (status == InstallStatus.DOWNLOADED) {
            appUpdateManager.completeUpdate()
        }
    }

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private var appUpdateInfo: AppUpdateInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instance = this

        val initial = computeInitial()
        setContent {
            App(initial)
        }
    }

    override fun onStart() {
        super.onStart()

        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                this.appUpdateInfo = appUpdateInfo
                Updates.updateAvailable.tryEmit(true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.registerListener(updateListener)
    }

    override fun onPause() {
        super.onPause()
        appUpdateManager.unregisterListener(updateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }


    private fun computeInitial(): Pair<EDataType, Long>? {
        val action: String? = intent?.action
        val data: Uri? = intent?.data
        val path: List<String>? = data?.pathSegments

        Napier.i { "Action: $action, data: $data" }

        return if (action == Intent.ACTION_VIEW && path != null) {
            val type = when (path.firstOrNull()) {
                "area" -> EDataType.AREA
                "zone" -> EDataType.ZONE
                "sector" -> EDataType.SECTOR
                "path" -> EDataType.PATH
                else -> null
            }
            val id = path.getOrNull(1)?.toLongOrNull()
            if (type != null && id != null) {
                type to id
            } else {
                null
            }
        } else {
            null
        }
    }


    /***
     * Requests the app update manager to install the latest version available.
     *
     * @return `true` if the update was requested, false otherwise.
     */
    fun installUpdate(): Boolean {
        if (appUpdateInfo == null) return false
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo!!,
            updateResultLauncher,
            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
        )
        return true
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}