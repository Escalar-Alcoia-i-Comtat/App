package org.escalaralcoiaicomtat.app.map.parser

import com.fleeksoft.ksoup.nodes.Element
import org.escalaralcoiaicomtat.app.map.style.Style

interface StyleParser <Type: Style> {
    fun parse(style: Element): Type?
}
