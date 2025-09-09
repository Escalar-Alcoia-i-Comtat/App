package org.escalaralcoiaicomtat.app.map.point

import org.escalaralcoiaicomtat.app.data.generic.LatLng

data class Region(
    val center: LatLng,
    val delta: Pair<Double, Double>
)
