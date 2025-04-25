package org.escalaralcoiaicomtat.app.map

import org.escalaralcoiaicomtat.app.map.placemark.Placemark
import org.escalaralcoiaicomtat.app.map.style.Style

data class MapData(
    val placemarks: List<Placemark>,
    val styles: List<Style>
)
