package org.escalaralcoiaicomtat.app.map.style

import androidx.compose.ui.graphics.Color
import com.fleeksoft.ksoup.nodes.Element
import org.escalaralcoiaicomtat.app.map.parser.StyleParser

class PolyStyle(
    override val id: String,
    override val lineColor: String,
    override val lineWidth: Double,
    val fillColor: String,
    val fill: Boolean,
    val outline: Boolean
): LineStyle(id, lineColor, lineWidth) {
    companion object: StyleParser<PolyStyle> {
        override fun parse(style: Element): PolyStyle? {
            val polyStyle = style.getElementsByTag("PolyStyle").firstOrNull()
            val lineStyle = LineStyle.parse(style)
            return if (lineStyle != null && polyStyle != null) {
                PolyStyle(
                    style.attr("id"),
                    lineStyle.lineColor,
                    lineStyle.lineWidth,
                    polyStyle.getElementsByTag("color")[0].text(),
                    polyStyle.getElementsByTag("fill")[0].text() == "1",
                    polyStyle.getElementsByTag("outline")[0].text() == "1"
                )
            } else {
                null
            }
        }
    }

    fun fillColor(): Color = Color(fillColor.hexToInt())

    override fun toString(): String {
        return "PolyStyle(id='$id', lineColor='$lineColor', lineWidth=$lineWidth, fillColor='$fillColor', fill=$fill, outline=$outline)"
    }
}
