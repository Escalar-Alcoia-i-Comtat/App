package org.escalaralcoiaicomtat.app.map.utils

import org.escalaralcoiaicomtat.app.data.generic.LatLng
import ovh.plrapps.mapcompose.api.BoundingBox
import ovh.plrapps.mapcompose.utils.Point

object RegionUtils {
    fun boundingBoxForPoints(coordinates: List<LatLng>): BoundingBox? {
        if (coordinates.isEmpty()) return null
        val points = coordinates.map { it.toNormalizedWebMercator() }
        return BoundingBox(
            xLeft = points.minOf { (x) -> x },
            xRight = points.maxOf { (x) -> x },
            yBottom = points.minOf { (_, y) -> y },
            yTop = points.maxOf { (_, y) -> y }
        )
    }

    fun computeCentroid(points: List<Point>): Point {
        val xSum = points.sumOf { (x) -> x }
        val ySum = points.sumOf { (_, y) -> y }
        return Point(xSum / points.size, ySum / points.size)
    }
}
