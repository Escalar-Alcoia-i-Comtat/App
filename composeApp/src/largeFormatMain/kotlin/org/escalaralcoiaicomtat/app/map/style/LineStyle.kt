package org.escalaralcoiaicomtat.app.map.style

import com.fleeksoft.ksoup.nodes.Element
import org.escalaralcoiaicomtat.app.map.parser.StyleParser

data class LineStyle(
    override val id: String,
    val color: String,
    val width: Double
): Style {
    companion object: StyleParser<LineStyle> {
        override fun parse(style: Element): LineStyle? {
            val lineStyle = style.getElementsByTag("LineStyle").firstOrNull()
            return if (lineStyle != null) {
                LineStyle(
                    style.attr("id"),
                    lineStyle.getElementsByTag("color")[0].value(),
                    lineStyle.getElementsByTag("width")[0].value().toDouble()
                )
            } else {
                null
            }
        }
    }
}
