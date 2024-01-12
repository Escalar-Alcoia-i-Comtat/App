package map.placemark

import com.fleeksoft.ksoup.nodes.Element
import map.parser.PlacemarkParser

data class Point(
    override val name: String,
    override val description: String?,
    override val styleUrl: String?,
    private val coordinates: String
): Placemark {
    companion object: PlacemarkParser<Point> {
        override fun parse(style: Element): Point? {
            return style.getElementsByTag("Point").firstOrNull()?.let { element ->
                Point(
                    style.getElementsByTag("name").first()!!.text(),
                    style.getElementsByTag("description").firstOrNull()?.text(),
                    style.getElementsByTag("styleUrl").firstOrNull()?.text(),
                    element.getElementsByTag("coordinates").first()!!.text()
                )
            }
        }
    }

    val latitude: Double = coordinates.split(',')[0].toDouble()
    val longitude: Double = coordinates.split(',')[1].toDouble()

    override fun addToPoints(list: MutableList<Pair<Double, Double>>) {
        list.add(
            latitude to longitude
        )
    }
}
