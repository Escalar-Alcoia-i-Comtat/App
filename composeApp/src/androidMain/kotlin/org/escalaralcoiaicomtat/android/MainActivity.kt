package org.escalaralcoiaicomtat.android

import App
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import data.EDataType
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {
    companion object {
        var instance: MainActivity? = null
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instance = this

        val initial = computeInitial()
        setContent {
            App(initial)
        }
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
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}