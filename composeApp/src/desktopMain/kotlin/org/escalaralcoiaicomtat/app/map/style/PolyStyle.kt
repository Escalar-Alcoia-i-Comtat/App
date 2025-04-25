package org.escalaralcoiaicomtat.app.map.style

import com.fleeksoft.ksoup.nodes.Element
import org.escalaralcoiaicomtat.app.map.parser.StyleParser

class PolyStyle(
    override val id: String,
    val lineColor: String,
    val lineWidth: Double,
    val fillColor: String,
    val fill: Boolean,
    val outline: Boolean
): Style {
    companion object: StyleParser<PolyStyle> {
        override fun parse(style: Element): PolyStyle? {
            val polyStyle = style.getElementsByTag("PolyStyle").firstOrNull()
            val lineStyle = LineStyle.parse(style)
            return if (lineStyle != null && polyStyle != null) {
                PolyStyle(
                    style.attr("id"),
                    lineStyle.color,
                    lineStyle.width,
                    polyStyle.getElementsByTag("color")[0].value(),
                    polyStyle.getElementsByTag("fill")[0].value() == "1",
                    polyStyle.getElementsByTag("outline")[0].value() == "1"
                )
            } else {
                null
            }
        }
    }
}
