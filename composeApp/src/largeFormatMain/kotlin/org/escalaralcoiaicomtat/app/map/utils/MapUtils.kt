package org.escalaralcoiaicomtat.app.map.utils

import io.github.aakira.napier.Napier
import ovh.plrapps.mapcompose.api.scale
import ovh.plrapps.mapcompose.api.scrollTo
import ovh.plrapps.mapcompose.api.snapScrollTo
import ovh.plrapps.mapcompose.ui.state.MapState
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.tan

fun latLonToNormalizedWebMercator(latitude: Double, longitude: Double): Pair<Double, Double> {
    val earthRadius = 6_378_137.0 // in meters
    val latRad = latitude * PI / 180.0
    val lngRad = longitude * PI / 180.0

    val x = earthRadius * lngRad
    val y = earthRadius * ln(tan((PI / 4.0) + (latRad / 2.0)))

    val piR = earthRadius * PI

    val normalizedX = (x + piR) / (2.0 * piR)
    val normalizedY = (piR - y) / (2.0 * piR)

    return Pair(normalizedX, normalizedY)
}

suspend fun MapState.scrollToLatLng(
    targetLat: Double,
    targetLng: Double,
    destScale: Double = scale
) {
    val (targetX, targetY) = latLonToNormalizedWebMercator(targetLat, targetLng)
    Napier.d { "Scrolling to $targetLat, $targetLng (tile $targetX, $targetY) at scale $destScale" }
    scrollTo(targetX, targetY, destScale)
}

suspend fun MapState.snapScrollToLatLng(
    targetLat: Double,
    targetLng: Double
) {
    val (targetX, targetY) = latLonToNormalizedWebMercator(targetLat, targetLng)
    Napier.d { "Snap scrolling to $targetLat, $targetLng (tile $targetX, $targetY)" }
    snapScrollTo(targetX, targetY)
}
