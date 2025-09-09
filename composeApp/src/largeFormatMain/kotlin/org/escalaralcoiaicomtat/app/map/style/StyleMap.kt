package org.escalaralcoiaicomtat.app.map.style

import com.fleeksoft.ksoup.nodes.Element

class StyleMap(
    val id: String,
    val pairs: List<Pair>
) {
    fun getNormalStyleUrl(): String? = pairs.find { it.key == Key.Normal }?.styleUrl
    fun getHighlightStyleUrl(): String? = pairs.find { it.key == Key.Highlight }?.styleUrl

    companion object {
        fun parse(style: Element): StyleMap? {
            val styleMap = style.getElementsByTag("StyleMap").firstOrNull()
            return if (styleMap != null) {
                val pairs = styleMap.getElementsByTag("Pair")
                    .mapNotNull { element ->
                        val keyString = element.getElementsByTag("key")[0].text()
                        val key = Key.fromString(keyString) ?: return@mapNotNull null
                        val styleUrl = element.getElementsByTag("styleUrl")[0].text()
                        Pair(key, styleUrl)
                    }
                StyleMap(
                    style.attr("id"),
                    pairs
                )
            } else {
                null
            }
        }
    }

    enum class Key {
        Normal, Highlight;

        companion object {
            fun fromString(string: String): Key? {
                return when (string) {
                    "normal" -> Normal
                    "highlight" -> Highlight
                    else -> null
                }
            }
        }
    }

    data class Pair(val key: Key, val styleUrl: String)
}
