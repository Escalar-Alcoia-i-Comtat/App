package map.kmz

import com.fleeksoft.ksoup.Ksoup
import io.github.aakira.napier.Napier
import map.MapData
import map.placemark.Placemark
import map.style.Style
import maps.KMZHandler
import kotlin.uuid.Uuid

object KMZLoader {
    suspend inline fun loadKMZ(
        kmzUUID: Uuid
    ): MapData {
        val kmlFile = KMZHandler.load(kmzUUID, replaceImagePaths = false)
        Napier.d { "KMZ file is ready. Reading KML..." }
        val kmlString = kmlFile.read("doc.kml")?.decodeToString()
            ?: throw IllegalStateException("KML file not found in KMZ")
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
        return MapData(placemarks, styles)
    }
}
