package utils.unit

interface DistanceUnit {
    val value: Double

    fun <U: DistanceUnit> convertTo(units: DistanceUnits): U
}
