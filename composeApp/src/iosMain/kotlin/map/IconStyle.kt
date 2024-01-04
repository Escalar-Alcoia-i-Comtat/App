package map

import com.fleeksoft.ksoup.nodes.Element
import map.parser.StyleParser

data class IconStyle(
    override val id: String,
    val scale: Float,
    val iconHref: String
): Style {
    companion object: StyleParser<IconStyle> {
        override fun parse(style: Element): IconStyle? {
            val iconStyle = style.getElementsByTag("IconStyle").firstOrNull()
            return if (iconStyle != null) {
                IconStyle(
                    style.attr("id"),
                    iconStyle.getElementsByTag("scale")[0].value().toFloat(),
                    iconStyle.getElementsByTag("Icon")[0].getElementsByTag("href")[0].value()
                )
            } else {
                null
            }
        }
    }
}
