package org.escalaralcoiaicomtat.app.map.style

import com.fleeksoft.ksoup.nodes.Element
import org.escalaralcoiaicomtat.app.map.parser.StyleParser

data class IconStyle(
    override val id: String,
    val scale: Float,
    val iconHref: String
): Style {
    companion object: StyleParser<IconStyle> {
        override fun parse(style: Element): IconStyle? {
            return style.getElementsByTag("IconStyle").firstOrNull()?.let { element ->
                val id = style.id()
                    .takeUnless { it.isEmpty() }
                    ?: return null
                val scale = element.getElementsByTag("scale")
                    .firstOrNull()
                    ?.text()
                    ?.toFloatOrNull()
                    ?: return null
                val iconHref = element.getElementsByTag("Icon")
                    .firstOrNull()
                    ?.getElementsByTag("href")
                    ?.firstOrNull()
                    ?.text()
                    ?: return null

                IconStyle(id, scale, iconHref)
            }
        }
    }
}
