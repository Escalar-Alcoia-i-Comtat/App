package map

import map.placemark.Placemark
import map.style.Style

data class MapData(
    val placemarks: List<Placemark>,
    val styles: List<Style>
)
