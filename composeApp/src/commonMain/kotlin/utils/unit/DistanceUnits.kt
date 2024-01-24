package utils.unit

import dev.icerock.moko.resources.StringResource
import resources.MR

enum class DistanceUnits(
    val label: StringResource,
    val valueFormat: StringResource,
    val factory: (Double) -> DistanceUnit
) {
    METER(
        MR.strings.unit_label_meter,
        MR.strings.unit_value_meter,
        { Meter(it) }
    ),
    FEET(
        MR.strings.unit_label_feet,
        MR.strings.unit_value_feet,
        { Feet(it) }
    )
}
