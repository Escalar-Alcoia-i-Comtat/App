package map.style

import com.fleeksoft.ksoup.nodes.Element
import map.parser.StyleParser

data class IconStyle(
    override val id: String,
    val scale: Float,
    val iconHref: String
): Style {
    companion object: StyleParser<IconStyle> {
        override fun parse(style: Element): IconStyle? {
            return style.getElementsByTag("IconStyle").firstOrNull()?.let { element ->
                IconStyle(
                    style.attr("id"),
                    element.getElementsByTag("scale")[0].value().toFloat(),
                    element.getElementsByTag("Icon")[0].getElementsByTag("href")[0].value()
                )
            }
        }
    }
}
