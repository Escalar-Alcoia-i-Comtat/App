package utils

import data.generic.LatLng
import kotlin.math.max
import kotlin.math.min

fun calculateCentralPoint(coordinates: List<LatLng>): Pair<LatLng, LatLng>? {
    if (coordinates.isEmpty()) return null

    var minLat = coordinates[0].latitude
    var maxLat = coordinates[0].latitude
    var minLon = coordinates[0].longitude
    var maxLon = coordinates[0].longitude

    for ((lat, lon) in coordinates) {
        minLat = min(minLat, lat)
        maxLat = max(maxLat, lat)
        minLon = min(minLon, lon)
        maxLon = max(maxLon, lon)
    }

    val centerLat = (minLat + maxLat) / 2
    val centerLon = (minLon + maxLon) / 2
    val latDelta = maxLat - minLat
    val lonDelta = maxLon - minLon

    return LatLng(centerLat, centerLon) to LatLng(latDelta, lonDelta)
}
