package org.escalaralcoiaicomtat.app.map.point

typealias LatLng = Pair<Double, Double>

val LatLng.latitude: Double get() = first

val LatLng.longitude: Double get() = second
