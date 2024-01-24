package utils.unit

data class Feet(
    override val value: Double
): ImperialUnit {
    @Suppress("UNCHECKED_CAST")
    override fun <U : DistanceUnit> convertTo(units: DistanceUnits): U {
        return when (units) {
            DistanceUnits.METER -> toMeter() as U
            DistanceUnits.FEET -> this as U
        }
    }
}

/**
 * 1 foot equals 0.3048 meters.
 */
private const val FootEqualsMeters = 0.3048

fun Feet.toMeter(): Meter = Meter(value * FootEqualsMeters)
