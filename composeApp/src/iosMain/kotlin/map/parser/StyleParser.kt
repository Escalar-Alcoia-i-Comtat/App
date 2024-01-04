package map.parser

import com.fleeksoft.ksoup.nodes.Element
import map.style.Style

interface StyleParser <Type: Style> {
    fun parse(style: Element): Type?
}
