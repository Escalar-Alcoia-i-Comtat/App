package org.escalaralcoiaicomtat.app.map.parser

import com.fleeksoft.ksoup.nodes.Element
import org.escalaralcoiaicomtat.app.map.placemark.Placemark

interface PlacemarkParser <Type: Placemark> {
    fun parse(style: Element): Type?
}
