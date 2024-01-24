package utils.unit

data class Meter(
    override val value: Double
): DistanceUnit

/**
 * 1 meter equals 3.2808399 feet.
 */
private const val MeterEqualsFeet = 3.2808399

fun Meter.toFeet(): Feet = Feet(value * MeterEqualsFeet)
