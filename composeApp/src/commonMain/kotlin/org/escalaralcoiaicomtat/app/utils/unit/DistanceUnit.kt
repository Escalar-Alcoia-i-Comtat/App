package org.escalaralcoiaicomtat.app.utils.unit

interface DistanceUnit {
    val value: Double

    fun <U: DistanceUnit> convertTo(units: DistanceUnits): U
}
