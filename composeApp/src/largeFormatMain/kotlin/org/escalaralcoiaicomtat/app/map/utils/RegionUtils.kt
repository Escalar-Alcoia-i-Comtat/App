package org.escalaralcoiaicomtat.app.map.utils

import io.github.aakira.napier.Napier
import org.escalaralcoiaicomtat.app.map.point.Region

object RegionUtils {
    private fun transform(c: Pair<Double, Double>): Pair<Double, Double> {
        return if (c.second < 0) {
            c.first to (360 + c.second)
        } else {
            c
        }
    }

    private fun inverseTransform(c: Pair<Double, Double>): Pair<Double, Double> {
        return if (c.second > 180) {
            c.first to (-360 + c.second)
        } else {
            c
        }
    }

    fun regionForPoints(coordinates: List<Pair<Double, Double>>): Region? {
        return if (coordinates.isEmpty()) {
            null
        } else if (coordinates.size == 1) {
            val center = coordinates[0]

            Region(
                center.first to center.second,
                1.0 to 1.0
            )
        } else {
            val transformed = coordinates.map(::transform)
            val minLat = transformed.minOf { it.first }
            val maxLat = transformed.maxOf { it.first }
            val minLon = transformed.minOf { it.second }
            val maxLon = transformed.maxOf { it.second }

            val latitudeDelta = maxLat - minLat
            val longitudeDelta = maxLon - minLon
            val span = latitudeDelta to longitudeDelta
            val center = inverseTransform(
                (maxLat - latitudeDelta / 2) to (maxLon - longitudeDelta / 2)
            )

            val (lon, lat) = center
            Napier.i { "Center is on ${lat},${lon}" }
            Region(lat to lon, span)
        }
    }
}
