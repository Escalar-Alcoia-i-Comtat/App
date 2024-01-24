package utils.unit

data class Feet(
    override val value: Double
): DistanceUnit

/**
 * 1 foot equals 0.3048 meters.
 */
private const val FootEqualsMeters = 0.3048

fun Feet.toMeter(): Meter = Meter(value * FootEqualsMeters)
