package org.escalaralcoiaicomtat.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.mmk.kmpnotifier.extensions.onCreateOrOnNewIntent
import com.mmk.kmpnotifier.notification.NotifierManager
import escalaralcoiaicomtat.composeapp.generated.resources.*
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.core.FileKit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.escalaralcoiaicomtat.app.AppRoot
import org.escalaralcoiaicomtat.app.platform.Updates
import org.escalaralcoiaicomtat.app.ui.navigation.Destination
import org.escalaralcoiaicomtat.app.ui.navigation.Destinations
import org.jetbrains.compose.resources.getString as getStringCMP

class MainActivity : AppCompatActivity() {
    companion object {
        var instance: MainActivity? = null
            private set
    }

    private val updateResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            Napier.e { "Update failed. Code: ${result.resultCode}" }

            CoroutineScope(Dispatchers.IO).launch {
                Updates.updateError.tryEmit(
                    getStringCMP(Res.string.update_error_update_code, result.resultCode.toString())
                )
            }
        }
    }

    private val updateListener = InstallStateUpdatedListener { state ->
        val status = state.installStatus()
        if (status == InstallStatus.DOWNLOADING) {
            Toast.makeText(
                this,
                getString(R.string.downloading_update_progress),
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

        // Send intent to handle notifications
        NotifierManager.onCreateOrOnNewIntent(intent)

        // Initialize the file picker
        FileKit.init(this)

        val startDestination = computeStartDestination()
        setContent {
            AppRoot(startDestination = startDestination)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Send intent to handle notifications
        NotifierManager.onCreateOrOnNewIntent(intent)
    }


    /**
     * Computes the initial route based on the intent data.
     *
     * The intent data is expected to be in the form of:
     * ```
     * https://domain.tld/{areaId?}/{zoneId?}/{sectorId?}?path={pathId?}
     * ```
     *
     * @return The initial destination or `null` if the intent data is not valid.
     */
    private fun computeStartDestination(): Destination? {
        val action: String? = intent?.action
        val data: Uri = intent?.data ?: return null

        Napier.i { "Action: $action, data: $data" }
        Napier.i { "Host: ${data.host}, fragment: ${data.fragment}" }

        return if (data.host?.equals("web.escalaralcoiaicomtat.org", true) == true) {
            Destinations.parse(data.fragment ?: return null)
                .takeUnless { it == Destinations.Intro || it is Destinations.Editor }
        } else {
            val pathSegments: List<Long> = data.pathSegments.mapNotNull(String::toLongOrNull)
            val pathId = data.getQueryParameter("path")?.toLongOrNull()

            return if (action == Intent.ACTION_VIEW && pathSegments.isNotEmpty()) {
                when (pathSegments.size) {
                    1 -> Destinations.Area(pathSegments[0])
                    2 -> Destinations.Zone(pathSegments[0], pathSegments[1])
                    3 -> Destinations.Sector(
                        pathSegments[0],
                        pathSegments[1],
                        pathSegments[2],
                        pathId
                    )

                    else -> null
                }
            } else {
                null
            }
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
    AppRoot()
}