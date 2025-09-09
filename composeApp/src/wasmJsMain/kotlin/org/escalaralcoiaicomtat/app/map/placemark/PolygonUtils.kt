package org.escalaralcoiaicomtat.app.map.placemark

import org.escalaralcoiaicomtat.app.map.point.toPair
import org.escalaralcoiaicomtat.app.map.utils.toNormalizedWebMercator
import ovh.plrapps.mapcompose.api.makePathDataBuilder
import ovh.plrapps.mapcompose.ui.paths.PathData
import ovh.plrapps.mapcompose.ui.state.MapState

fun Polygon.pathData(state: MapState): PathData? {
    var builder = state.makePathDataBuilder()
    for (point in coordinates) {
        val (x, y) = point.toNormalizedWebMercator().toPair()
        builder = builder.addPoint(x, y)
    }
    return builder.build()
}
