package org.escalaralcoiaicomtat.app.map.kmz

import com.fleeksoft.ksoup.Ksoup
import io.github.aakira.napier.Napier
import org.escalaralcoiaicomtat.app.map.MapData
import org.escalaralcoiaicomtat.app.map.placemark.Placemark
import org.escalaralcoiaicomtat.app.map.placemark.Point
import org.escalaralcoiaicomtat.app.map.style.IconStyle
import org.escalaralcoiaicomtat.app.map.style.Style
import org.escalaralcoiaicomtat.app.map.style.StyleMap
import org.escalaralcoiaicomtat.app.maps.KMZHandler
import kotlin.uuid.Uuid

object KMZLoader {
    suspend inline fun loadKMZ(
        kmzUUID: Uuid
    ): MapData {
        val kmzFile = KMZHandler.load(kmzUUID, replaceImagePaths = false)
        Napier.d { "KMZ file is ready. Reading KML..." }
        val kmlString = kmzFile.read("doc.kml")?.decodeToString()
            ?: throw IllegalStateException("KML file not found in KMZ")
        val xml = Ksoup.parseXml(kmlString)
        val document = xml.root().getElementsByTag("Document")[0]
        val styles = document.getElementsByTag("Style")
            .also { Napier.d { "Processing ${it.size} style elements..." } }
            .mapNotNull(Style.Companion::parse)
            .also { Napier.d { "Loaded ${it.size} style elements." } }
        val styleMaps = document.getElementsByTag("StyleMap")
            .also { Napier.d { "Processing ${it.size} style map elements..." } }
            .mapNotNull(StyleMap.Companion::parse)
            .also { Napier.d { "Loaded ${it.size} style map elements." } }
        val placemarks = document.getElementsByTag("Folder")
            .also { Napier.d { "Processing ${it.size} folders..." } }
            .flatMap { folder ->
                folder.getElementsByTag("Placemark").map { Placemark.Companion.parse(it) }
            }
            .filterNotNull()

        val iconStyleData = placemarks.filterIsInstance<Point>().mapNotNull { point ->
            val style = styles.filterIsInstance<IconStyle>()
                .find { point.styleUrl?.endsWith(it.id) == true }
                ?: return@mapNotNull null
            style.iconHref to kmzFile.read(style.iconHref)
        }.toMap()

        return MapData(placemarks, styles, styleMaps, iconStyleData)
    }
}
