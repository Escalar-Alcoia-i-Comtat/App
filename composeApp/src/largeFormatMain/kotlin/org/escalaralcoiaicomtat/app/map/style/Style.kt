package org.escalaralcoiaicomtat.app.map.style

import com.fleeksoft.ksoup.nodes.Element
import io.github.aakira.napier.Napier

interface Style {
    companion object {
        fun parse(style: Element): Style? {
            try {
                IconStyle.parse(style)?.let { return it }
                PolyStyle.parse(style)?.let { return it }
                LineStyle.parse(style)?.let { return it }
            } catch (e: Exception) {
                Napier.e(e) { "Could not parse style element: $style" }
            }
            return null
        }
    }

    val id: String
}
