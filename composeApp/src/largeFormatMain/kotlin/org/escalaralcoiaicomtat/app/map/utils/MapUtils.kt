package org.escalaralcoiaicomtat.app.map.utils

import org.escalaralcoiaicomtat.app.data.generic.LatLng
import ovh.plrapps.mapcompose.utils.Point
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.tan
import org.escalaralcoiaicomtat.app.map.placemark.Point as PlacemarkPoint

fun latLonToNormalizedWebMercator(latitude: Double, longitude: Double): Point {
    val earthRadius = 6_378_137.0 // in meters
    val latRad = latitude * PI / 180.0
    val lngRad = longitude * PI / 180.0

    val x = earthRadius * lngRad
    val y = earthRadius * ln(tan((PI / 4.0) + (latRad / 2.0)))

    val piR = earthRadius * PI

    val normalizedX = (x + piR) / (2.0 * piR)
    val normalizedY = (piR - y) / (2.0 * piR)

    return Point(normalizedX, normalizedY)
}

fun LatLng.toNormalizedWebMercator(): Point = latLonToNormalizedWebMercator(latitude, longitude)

fun PlacemarkPoint.locationToNormalizedWebMercator(): Point = location.toNormalizedWebMercator()
