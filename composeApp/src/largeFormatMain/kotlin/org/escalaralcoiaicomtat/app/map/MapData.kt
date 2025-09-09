package org.escalaralcoiaicomtat.app.map

import org.escalaralcoiaicomtat.app.map.placemark.Placemark
import org.escalaralcoiaicomtat.app.map.style.Style
import org.escalaralcoiaicomtat.app.map.style.StyleMap

data class MapData(
    val placemarks: List<Placemark>,
    val styles: List<Style>,
    val styleMaps: List<StyleMap>,
    val iconStyleData: Map<String, ByteArray?>
) {
    fun findStyleByUrl(url: String?): Style? {
        if (url == null) return null
        return styles.find { url.endsWith(it.id) }
    }
    fun findStyleMapByUrl(url: String?): StyleMap? {
        if (url == null) return null
        return styleMaps.find { url.endsWith(it.id) }
    }
}
