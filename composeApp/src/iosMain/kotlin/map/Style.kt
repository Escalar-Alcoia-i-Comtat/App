package map

import com.fleeksoft.ksoup.nodes.Element

interface Style {
    companion object {
        fun parse(style: Element): Style? {
            val iconStyle = IconStyle.parse(style)
            val polyStyle = PolyStyle.parse(style)
            val lineStyle = LineStyle.parse(style)
            return when {
                iconStyle != null -> iconStyle
                polyStyle != null -> polyStyle
                lineStyle != null -> lineStyle
                else -> null
            }
        }
    }

    val id: String
}
