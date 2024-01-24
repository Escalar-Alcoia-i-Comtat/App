package utils.unit

data class Meter(
    override val value: Double
): ImperialUnit {
    @Suppress("UNCHECKED_CAST")
    override fun <U : DistanceUnit> convertTo(units: DistanceUnits): U {
        return when (units) {
            DistanceUnits.METER -> this as U
            DistanceUnits.FEET -> toFeet() as U
        }
    }
}

/**
 * 1 meter equals 3.2808399 feet.
 */
private const val MeterEqualsFeet = 3.2808399

fun Meter.toFeet(): Feet = Feet(value * MeterEqualsFeet)

val Float.meters: Meter get() = Meter(toDouble())
val Double.meters: Meter get() = Meter(toDouble())
val Int.meters: Meter get() = Meter(toDouble())
val Long.meters: Meter get() = Meter(toDouble())
val UInt.meters: Meter get() = Meter(toDouble())
val ULong.meters: Meter get() = Meter(toDouble())
