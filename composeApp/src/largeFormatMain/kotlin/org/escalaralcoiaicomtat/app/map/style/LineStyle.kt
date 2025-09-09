package org.escalaralcoiaicomtat.app.map.style

import androidx.compose.ui.graphics.Color
import com.fleeksoft.ksoup.nodes.Element
import org.escalaralcoiaicomtat.app.map.parser.StyleParser

open class LineStyle(
    override val id: String,
    open val lineColor: String,
    open val lineWidth: Double
): Style {
    companion object: StyleParser<LineStyle> {
        override fun parse(style: Element): LineStyle? {
            val lineStyle = style.getElementsByTag("LineStyle").firstOrNull()
            return if (lineStyle != null) {
                LineStyle(
                    style.attr("id"),
                    lineStyle.getElementsByTag("color")[0].text(),
                    lineStyle.getElementsByTag("width")[0].text().toDouble()
                )
            } else {
                null
            }
        }
    }

    fun lineColor(): Color = Color(lineColor.hexToInt())

    override fun toString(): String {
        return "LineStyle(id='$id', lineColor='$lineColor', lineWidth=$lineWidth)"
    }
}
