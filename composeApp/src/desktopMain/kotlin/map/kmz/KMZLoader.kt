package map.kmz

import com.fleeksoft.ksoup.Ksoup
import io.github.aakira.napier.Napier
import map.MapData
import map.placemark.Placemark
import map.style.Style
import maps.KMZHandler

object KMZLoader {
    suspend inline fun loadKMZ(
        kmzUUID: String,
        onDocumentLoaded: (data: MapData) -> Unit
    ) {
        val kmlFile = KMZHandler.load(kmzUUID, replaceImagePaths = false)
        val kmlString = kmlFile.readAllBytes().decodeToString()
        val xml = Ksoup.parse(kmlString)
        val document = xml.root().getElementsByTag("Document")[0]
        val styles = document.getElementsByTag("Style")
            .also { Napier.d { "Processing ${it.size} style elements..." } }
            .mapNotNull(Style::parse)
        val placemarks = document.getElementsByTag("Folder")
            .also { Napier.d { "Processing ${it.size} folders..." } }
            .flatMap { folder ->
                folder.getElementsByTag("Placemark").map { Placemark.parse(it) }
            }
            .filterNotNull()
        onDocumentLoaded(
            MapData(placemarks, styles)
        )
    }
}
