package org.escalaralcoiaicomtat.app.ui.platform

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import com.fleeksoft.ksoup.Ksoup
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.escalaralcoiaicomtat.app.map.placemark.Placemark
import org.escalaralcoiaicomtat.app.map.style.Style
import org.escalaralcoiaicomtat.app.maps.KMZHandler
import org.escalaralcoiaicomtat.app.ui.model.ViewModelBase
import kotlin.uuid.Uuid

actual class MapViewModel actual constructor() : ViewModelBase() {

    private val _error = MutableStateFlow<Throwable?>(null)
    val error: StateFlow<Throwable?> get() = _error.asStateFlow()

    actual val supportsZoomButtons: Boolean = false

    actual fun zoomIn() { }

    actual fun zoomOut() { }

    fun loadKMZ(
        kmz: Uuid,
        onDocumentLoaded: (data: MapData) -> Unit
    ) {
        launch {
            _error.emit(null)
            try {
                val kmlFile = KMZHandler.load(kmz, replaceImagePaths = false)
                val kmlString = kmlFile.read("doc.kml")!!.decodeToString()
                val xml = Ksoup.parse(kmlString)
                val document = xml.root().getElementsByTag("Document")[0]
                val styles = document.getElementsByTag("Style")
                    .also { Napier.d { "Processing ${it.size} style elements..." } }
                    .mapNotNull(Style.Companion::parse)
                val placemarks = document.getElementsByTag("Folder")
                    .also { Napier.d { "Processing ${it.size} folders..." } }
                    .flatMap { folder ->
                        folder.getElementsByTag("Placemark").map { Placemark.parse(it) }
                    }
                    .filterNotNull()
                onDocumentLoaded(
                    MapData(placemarks, styles)
                )
            } catch (e: Exception) {
                Napier.e(e) { "Could not load map." }
                _error.tryEmit(e)
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun copyException(clipboard: Clipboard) {
        val exception = _error.value ?: return
        launch {
            clipboard.setClipEntry(
                ClipEntry.withPlainText(exception.toString())
            )
        }
    }
}
