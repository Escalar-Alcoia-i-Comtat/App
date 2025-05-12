package org.escalaralcoiaicomtat.app.ui.platform

import com.fleeksoft.ksoup.Ksoup
import io.github.aakira.napier.Napier
import org.escalaralcoiaicomtat.app.map.placemark.Placemark
import org.escalaralcoiaicomtat.app.map.style.Style
import org.escalaralcoiaicomtat.app.maps.KMZHandler
import org.escalaralcoiaicomtat.app.ui.model.ViewModelBase
import kotlin.uuid.Uuid

actual class MapViewModel actual constructor() : ViewModelBase() {
    fun loadKMZ(
        kmz: Uuid,
        onDocumentLoaded: (data: MapData) -> Unit
    ) {
        launch {
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
                    folder.getElementsByTag("Placemark").map { Placemark.Companion.parse(it) }
                }
                .filterNotNull()
            onDocumentLoaded(
                MapData(placemarks, styles)
            )
        }
    }
}
