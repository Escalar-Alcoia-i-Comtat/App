package org.escalaralcoiaicomtat.app.map.placemark

import com.fleeksoft.ksoup.nodes.Element
import org.escalaralcoiaicomtat.app.map.parser.PlacemarkParser

data class Polygon(
    override val name: String,
    override val description: String?,
    override val styleUrl: String?,
    val coordinates: List<Pair<Double, Double>>
): Placemark {
    companion object: PlacemarkParser<Polygon> {
        override fun parse(style: Element): Polygon? {
            return style.getElementsByTag("Polygon").firstOrNull()?.let { element ->
                val coordinates = element.getElementsByTag("outerBoundaryIs")[0]
                    .getElementsByTag("LinearRing")[0]
                    .getElementsByTag("coordinates")[0]
                    .text()
                    .split(" ")
                    .map { it.trim().split(',') }
                    .map { it[0].toDouble() to it[1].toDouble() }

                Polygon(
                    style.getElementsByTag("name").first()!!.text(),
                    style.getElementsByTag("description").firstOrNull()?.text(),
                    style.getElementsByTag("styleUrl").firstOrNull()?.text(),
                    coordinates
                )
            }
        }
    }

    override fun addToPoints(list: MutableList<Pair<Double, Double>>) {
        list.addAll(coordinates)
    }
}
