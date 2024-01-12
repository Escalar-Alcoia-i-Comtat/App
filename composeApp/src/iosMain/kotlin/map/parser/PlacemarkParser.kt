package map.parser

import com.fleeksoft.ksoup.nodes.Element
import map.placemark.Placemark

interface PlacemarkParser <Type: Placemark> {
    fun parse(style: Element): Type?
}
