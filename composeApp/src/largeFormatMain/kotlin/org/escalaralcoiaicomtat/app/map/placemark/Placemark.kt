package org.escalaralcoiaicomtat.app.map.placemark

import com.fleeksoft.ksoup.nodes.Element
import org.escalaralcoiaicomtat.app.data.generic.LatLng

interface Placemark {
    companion object {
        fun parse(placemark: Element): Placemark? {
            val point = Point.parse(placemark)
            val polygon = Polygon.parse(placemark)
            return when {
                point != null -> point
                polygon != null -> polygon
                else -> null
            }
        }
    }

    val name: String
    val description: String?
    val styleUrl: String?

    fun addToPoints(list: MutableList<LatLng>)

    fun generateId(): String
}
